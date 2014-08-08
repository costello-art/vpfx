package player;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


public class RWImageView extends ImageView {
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();

    public RWImageView(double width, double height) {
        this.width.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateImageSize();
            }
        });

        this.height.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateImageSize();
            }
        });

        this.width.set(width);
        this.height.set(height);
    }

    @Override
    public void resize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    private void updateImageSize() {
        int width = (int) getWidth();
        int height = (int) getHeight();

        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }

        setImage(new WritableImage(width, height));
    }

    public final double getWidth() {
        return width.get();
    }

    public final void setWidth(double width) {
        this.width.set(width);
    }

    public final double getHeight() {
        return height.get();
    }

    public final void setHeight(double height) {
        this.height.set(height);
    }

    public PixelWriter getPixelWriter() {
        return ((WritableImage) getImage()).getPixelWriter();
    }
}