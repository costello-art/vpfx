package player;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.log4j.Logger;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple video player
 */
public class VideoPlayer extends BorderPane {

    /**
     * Logger to log everything into file. See config file for more info
     */
    private static Logger logger = Logger.getLogger(VideoPlayer.class);
    /**
     * Pixel format
     */
    final private WritablePixelFormat<ByteBuffer> byteBgraInstance = PixelFormat.getByteBgraPreInstance();
    /**
     * Camera name
     */
    protected Label cameraHeader;
    protected String cameraName = "[name]";
    protected String soundText = "[sound]";
    /**
     * Canvas, where you'll see video
     */
    protected RWImageView canvas;
    /**
     * Media player component
     */
    DirectMediaPlayer mp;
    /**
     * Pixel writer to write on WritableImage
     */
    private PixelWriter pixelWriter = null;
    /**
     * Inner pane, that will be put in BorderPane. ImageView will watch for its
     * (inner pane) sizes and will match them, not parent BorderPane, to prevent
     * overflow.
     */
    private StackPane inner;
    /**
     * Important player events (videoOutput, error etc)
     */
    private MediaPlayerEventListener mediaPlayerEventListener;

    /**
     * Default player.
     *
     * @param title camera name
     */
    public VideoPlayer(String title) {

        List<String> arguments = new ArrayList<>();

        arguments.add("--no-plugins-cache");
        arguments.add("--no-snapshot-preview");
        arguments.add("--input-fast-seek");
        arguments.add("--no-video-title-show");
        arguments.add("--disable-screensaver");
        arguments.add("--network-caching");
        arguments.add("1000");
        arguments.add("--quiet");
        arguments.add("--quiet-synchro");
        arguments.add("--intf");
        arguments.add("dummy");
        arguments.add("--sout-keep");
        arguments.add("--verbose=2");

        canvas = new RWImageView(16, 16);
        pixelWriter = canvas.getPixelWriter();


        MediaPlayerFactory factory = new MediaPlayerFactory(arguments);

        mp = factory.newDirectMediaPlayer(
                new BufferFormatCallback() {
                    @Override
                    public BufferFormat getBufferFormat(final int width, final int height) {
                        logger.debug(String.format("New buffer format: %dx%d", width, height));

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                canvas.resize(width, height);
                                pixelWriter = canvas.getPixelWriter();
                            }
                        });

                        return new RV32BufferFormat(width, height);
                    }
                },
                new RenderCallback() {
                    @Override
                    public void display(DirectMediaPlayer mp, Memory[] memory, final BufferFormat bufferFormat) {
                        final ByteBuffer byteBuffer = memory[0].getByteBuffer(0, memory[0].size());

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pixelWriter.setPixels(
                                        0,
                                        0,
                                        bufferFormat.getWidth(),
                                        bufferFormat.getHeight(),
                                        byteBgraInstance,
                                        byteBuffer,
                                        bufferFormat.getPitches()[0]);
                            }
                        });
                    }
                }
        );

        inner = new StackPane();
        inner.setMinSize(12, 8);
        this.setCenter(inner);
        this.setMinSize(80, 60);

        canvas.fitWidthProperty().bind(inner.widthProperty());
        canvas.fitHeightProperty().bind(inner.heightProperty());
        canvas.setPreserveRatio(true);

        cameraName = title != null ? title : "no name";
        cameraHeader = new Label(cameraName);

        this.setTop(cameraHeader);
        inner.getChildren().add(canvas);

        addVideoOutputEvents();
    }

    /**
     * Play video
     *
     * @param path video to play (can be anything)
     */
    public void play(String path) {
        logger.debug("Playing " + path);
        mp.playMedia(path);
    }

    protected void updateTitle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cameraHeader.setText(cameraName + " " + soundText);
            }
        });
    }

    /**
     * Stop play.
     */
    public void stop() {
        if (mp.isPlaying()) {
            mp.stop();
        } else {
            logger.debug("Player <" + cameraHeader.getText() + "> is stopped or error");
        }
    }

    /**
     * Release media player.
     */
    public void release() {
        stop();
        mp.release();
    }

    /**
     * Set player volume
     *
     * @param volume
     */
    public void setVolume(int volume) {
        mp.setVolume(volume);
    }

    /**
     * Disable sound.
     */
    public void mute() {
        mp.setVolume(0);
    }

    /**
     * Enable sound
     */
    public void unMute() {
        mp.setVolume(100);
    }

    /**
     * Reset volume. If no sound - enable, if sound - disable.
     */
    public void resetVolume() {
        logger.debug("sound value: " + mp.getVolume());
        //-1: no sound yet, 0: sound is muted

        int sound = mp.getVolume();

        if (sound < 0) {
            soundText = "[без звуку]";
            updateTitle();
            return;
        }

        if (sound <= 0) {
            unMute();
            soundText = "[sound enabled]";
            updateTitle();
            return;
        }

        mute();
        soundText = "[sound disabled]";
        updateTitle();
    }

    /**
     * Add some events when video output has been started.
     * <p/>
     * For example, mute sound. it is not possible to set volume BEFORE video
     * output.
     */
    private void addVideoOutputEvents() {
        mediaPlayerEventListener = new MediaPlayerEventAdapter() {

            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int i) {
                logger.debug(cameraHeader.getText() + ": video output has been created.");
                resetVolume();
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stop();
                        logger.error(cameraHeader.getText() + ": error while playing video.");
                    }
                });
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                logger.debug(cameraHeader.getText() + ": playing event");
            }
        };

        mp.addMediaPlayerEventListener(mediaPlayerEventListener);
    }
}