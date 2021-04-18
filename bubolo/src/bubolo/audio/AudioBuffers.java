package bubolo.audio;

import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGetError;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.AL10.alSourcei;

import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.backends.lwjgl3.audio.OggInputStream;

/**
 * Generates and manages OpenAL buffers.
 *
 * @author Christopher D. Canfield
 */
class AudioBuffers {
	private final int[] bufferIds;

	/**
	 * Generates one OpenAL buffer for each sound effect in the Sfx enum.
	 */
	AudioBuffers() {
		// Reset error state.
		alGetError();

		// Generate the buffers.
		bufferIds = new int[Sfx.values().length];
		alGenBuffers(bufferIds);

		// Check for errors.
		int errorCode = alGetError();
		if (errorCode != 0) {
			String errorText = alGetString(errorCode);
			throw new GameAudioException(String.format("OpenAL error %s (%d) when attempting to generate %d buffers.", errorText, errorCode, bufferIds.length));
		}
	}

	/**
	 * Loads audio data from an ogg file into an OpenAL buffer.
	 *
	 * @param soundEffect the sound effect to load.
	 * @param folderPath the location of the sound file.
	 */
	void loadOgg(Sfx soundEffect, String folderPath) {
		var path = Paths.get(folderPath, soundEffect.fileName);
		try (OggInputStream oggStream = new OggInputStream(Files.newInputStream(path))) {
			byte[] pcm = decodeOgg(oggStream);
			fillOpenALBuffer(soundEffect, pcm, oggStream.getChannels(), oggStream.getSampleRate());
		} catch (Exception e) {
			throw new GameAudioException(e);
		}
	}

	/**
	 * The following method is adapted from libGDX com.badlogic.gdx.backends.lwjgl3.audio.Ogg:
	 *
	 /*******************************************************************************
	 * Copyright 2011 Mario Zechner <badlogicgames@gmail.com> & Nathan Sweet <nathan.sweet@gmail.com>
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this method except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *   http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 ******************************************************************************
	 *
	 * @param stream the ogg input stream to read.
	 * @return a byte array containing the pcm data.
	 */
	private static byte[] decodeOgg(OggInputStream stream) {
		ByteArrayOutputStream data = new ByteArrayOutputStream(4096);
		byte[] buffer = new byte[2048];
		while (!stream.atEnd()) {
			int length = stream.read(buffer);
			if (length == -1) break;
			data.write(buffer, 0, length);
		}
		return data.toByteArray();
	}

	/**
	 * The following method is adapted from libGDX com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound:
	 *
	 /*******************************************************************************
	 * Copyright 2011 Mario Zechner <badlogicgames@gmail.com> & Nathan Sweet <nathan.sweet@gmail.com>
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this method except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *   http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 ******************************************************************************
	 *
	 */
	private void fillOpenALBuffer(Sfx soundEffect, byte[] pcm, int channels, int sampleRate) {
		int bytes = pcm.length - (pcm.length % (channels > 1 ? 4 : 2));

		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(pcm, 0, bytes);
		((Buffer) buffer).flip();

		int bufferID = bufferIds[soundEffect.ordinal()];
		alBufferData(bufferID, channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, buffer.asShortBuffer(), sampleRate);
	}

	void attachBufferToSource(Sfx soundEffect, int source) {
		alSourcei(source, AL10.AL_BUFFER, bufferIds[soundEffect.ordinal()]);

		int errorCode = alGetError();
		if (errorCode != 0) {
			String errorText = alGetString(errorCode);
			throw new GameAudioException(
					String.format("OpenAL error %s (%d) when attempting to attach buffer %d to source %d.", errorText, errorCode, source, soundEffect.ordinal()));
		}
	}

	void dispose() {
		alDeleteBuffers(bufferIds);
	}
}
