package editor.object;

import ws.schild.jave.*;

import javax.sound.sampled.*;
import java.io.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

// https://github.com/goxr3plus/Java-Audio-Wave-Spectrum-API/blob/master/src/soundcloud/BoxWaveform.java

public class Audio {
    static final JFXPanel fxPanel = new JFXPanel(); // apparently you need to initialize JavaFX before using JavaFX things (like Media/MediaPlayer)
    static final int SAMPLE_RATE = 44100;

    private File wav;
    private MediaPlayer mediaPlayer;
    private float[] samples;

    public Audio(File sourceFile) {
        // convert to wav if needed (this is for drawing amplitude waveform)
        if (sourceFile.getName().endsWith(".wav")) {
            wav = sourceFile;
        } else {
            try {
                long t = System.currentTimeMillis();
                wav = File.createTempFile("decoded_audio", ".wav");
                transcodeToWav(sourceFile, wav);
                wav.deleteOnExit();
                System.out.println("[wav] duration ms: " + (System.currentTimeMillis() - t));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fatal exception exiting....");
                System.exit(0);
            }
        }
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(wav);
            AudioFormat fmt = in.getFormat();
            if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                throw new UnsupportedAudioFileException("unsigned");
            }
            boolean big = fmt.isBigEndian();
            int chs = fmt.getChannels();
            int bits = fmt.getSampleSizeInBits();
            int bytes = bits + 7 >> 3;
            int frameLength = (int) in.getFrameLength();
            int bufferLength = chs * bytes * 1024;
            samples = new float[frameLength];
            byte[] buf = new byte[bufferLength];
            int frame = 0;
            int bRead;
            System.out.println(samples.length);
            while ((bRead = in.read(buf)) > -1) {
                for (int b = 0; b < bRead; ) {
                    double sum = 0;
                    for (int c = 0; c < chs; c++) {
                        if (bytes == 1) {
                            sum += buf[b++] << 8;
                        } else {
                            int sample = 0;
                            if (big) {
                                sample |= (buf[b++] & 0xFF) << 8;
                                sample |= (buf[b++] & 0xFF);
                                b += bytes - 2;
                            } else {
                                b += bytes - 2;
                                sample |= (buf[b++] & 0xFF);
                                sample |= (buf[b++] & 0xFF) << 8;
                            }
                            final int sign = 1 << 15;
                            final int mask = -1 << 16;
                            if ((sample & sign) == sign) {
                                sample |= mask;
                            }
                            sum += sample;
                        }
                    }
                    samples[frame++] = (float) (sum / chs);
                }
            }
            float normal = 0;
            for (float sample : samples)
                if (sample > normal)
                    normal = sample;
            normal = 32768.0f / normal; // normalized scaling
            for (int i = 0; i < samples.length; i++) samples[i] *= normal;

            try {
                long startTime = System.currentTimeMillis();
                Media media = new Media(wav.toURI().toURL().toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                System.out.println("[Clip] duration ms: " + (System.currentTimeMillis() - startTime));
            } catch (IOException ex) {
                System.out.println("Error playing the audio file.");
                ex.printStackTrace();
            }
        } catch (Exception e) {
            wav.delete();
            wav.deleteOnExit();
            e.printStackTrace();
        }
    }

    /**
     * Transcode to Wav
     *
     * @param sourceFile
     * @param destinationFile
     * @throws EncoderException
     */
    private static void transcodeToWav(File sourceFile, File destinationFile) throws Exception {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setChannels(2);
        audio.setSamplingRate(SAMPLE_RATE);
        EncodingAttributes attributes = new EncodingAttributes();
        attributes.setFormat("wav");
        attributes.setAudioAttributes(audio);
        new Encoder().encode(new MultimediaObject(sourceFile), destinationFile, attributes);
    }

    public File getFile() {
        return wav;
    }

    public float[] getSamples() {
        return samples;
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
}
