package tm.ui;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.DRAWITEMSTRUCT;

import tm.lib.engine.*;

public class PitchWidget extends Canvas {
    
    private static final int HMARGIN = 5;
    private static final int VMARGIN = 10;
    
    private static final double PLAYER_SIZE = 15;
    private static final double BALL_SIZE = 6;
    private static final double TARGET_SIZE = 2;
    private static final double VISIBLE_TARGET_SIZE = 8;

    private static final boolean DRAW_BLOCKED_ZONES = true;

    private Label topLabel;
    private Label bottomLabel;
    private Pitch pitch;
    private Point pitchSize;
    private Point pitchPos;

    private final Color PITCH_COLOR = new Color(TenisMasters.display, 254, 251, 126);
    private final Color DARK_PITCH_COLOR = new Color(TenisMasters.display, 203, 200, 75);
    private final Color LABEL_COLOR = TenisMasters.display.getSystemColor(SWT.COLOR_WHITE);
    private Font font;
    //Image buffer;

    public PitchWidget(Composite parent, Pitch p) {
        super(parent, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        pitch = p;

        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        setLayout(layout);

        topLabel = new Label(this, SWT.CENTER | SWT.BORDER);
        topLabel.setText("---");
        //top_label.setBackground(PITCH_COLOR);
        topLabel.setBackground(LABEL_COLOR);

        GridData data = new GridData();
        //data.horizontalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.END;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        topLabel.setLayoutData(data);

        bottomLabel = new Label(this, SWT.CENTER | SWT.BORDER);
        bottomLabel.setText("+++");
        bottomLabel.setBackground(LABEL_COLOR);
        data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.BEGINNING;
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        bottomLabel.setLayoutData(data);

        FontData[] font_data = topLabel.getFont().getFontData();
        font_data[0].setHeight(18);
        font = new Font(TenisMasters.display, font_data[0]);
        topLabel.setFont(font);
        bottomLabel.setFont(font);

        topLabel.setVisible(false);
        bottomLabel.setVisible(false);

        pack();

        //buffer = new Image(TenisMasters.display, getClientArea());
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                font.dispose();
                //buffer.dispose();
            }
        });

        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                //update_buffer();
                //buffer = new Image(TenisMasters.display, getClientArea().width, getClientArea().height);
                //GC gc = new GC(buffer);
                //draw_pitch(gc);
                //e.gc.drawImage(buffer, 0, 0);
                //buffer = new Image(TenisMasters.display, getClientArea());
                drawPitch(e.gc);
            }
        });
    }

    public void setUpperText(String text) {
        topLabel.setText(text);
        topLabel.pack();
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.END;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        topLabel.setLayoutData(data);
        topLabel.setVisible(true);
        layout();
    }

    public void setBottomText(String text) {
        bottomLabel.setText(text);
        bottomLabel.pack();
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        data.verticalAlignment = GridData.BEGINNING;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        bottomLabel.setLayoutData(data);
        bottomLabel.setVisible(true);
        layout();
    }

    private void updatePitchPos() {
        Point s = getSize();
        s.x -= 2 * HMARGIN;
        s.y -= 2 * VMARGIN;

        Vector2D psize = new Vector2D(Pitch.WIDTH, Pitch.HEIGHT);
        double coef = s.y / Pitch.HEIGHT;
        psize = psize.scalarMultiply(coef);

        if ((int) psize.getX() <= s.x) {
            pitchSize = VectorUtils.toPoint(psize);
            pitchPos = new Point((s.x - pitchSize.x) / 2 + HMARGIN, VMARGIN);
        } else {
            coef = s.x / Pitch.WIDTH;
            psize = new Vector2D(Pitch.WIDTH, Pitch.HEIGHT);
            psize = psize.scalarMultiply(coef);
            pitchSize = VectorUtils.toPoint(psize);
            pitchPos = new Point(HMARGIN, (s.y - pitchSize.y) / 2 + VMARGIN);
        }
    }

    private Point mapPos(double x, double y) {
        double coef = getPitchSizeCoef();
        int px = (int) (x * coef + pitchPos.x);
        int py = (int) ((Pitch.HALF_HEIGHT - y) * coef) + pitchPos.y;
        return new Point(px, py);
    }

    private Point mapPos(Vector2D pos) {
        return mapPos(pos.getX(), pos.getY());
    }

    private Rectangle mapRect(double x1, double y1, double x2, double y2) {
        Point p1 = mapPos(x1, y1);
        Point p2 = mapPos(x2, y2);
        return new Rectangle(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
    }

    private Rectangle mapRect(Vector2D topLeft, Vector2D bottomRight) {
        return mapRect(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    private double getPitchSizeCoef() {
        return pitchSize.x / Pitch.WIDTH;
    }

    /*public void update_buffer() {
     Rectangle a = this.getClientArea();
     Rectangle b = buffer.getBounds();
		
     if (a.width != b.width || a.height != b.height) {
     buffer.dispose();
     buffer = new Image(TenisMasters.display, a);
     buffer.setBackground(getBackground());
     }
     }*/
    public void update() {
        redraw();

        /*final Image buffer = new Image(TenisMasters.display, this.getClientArea());
         GC gc = new GC(buffer);		
         draw_pitch(gc);
         GC pitch_gc = new GC(this);
         pitch_gc.drawImage(buffer, 0, 0);
         buffer.dispose();
         gc.dispose();
         pitch_gc.dispose();*/
        //update_buffer();
        /*draw_pitch_borders(gc);
         draw_player(gc, pitch.player_1);
         draw_player(gc, pitch.player_2);
         draw_ball(gc);*/
        //draw_pitch(gc);
        //update_pitch_pos();
        //this.redraw(pitch_pos.x, pitch_pos.y, pitch_size.x, pitch_size.y, false);
        /*gc.setBackground(PITCH_COLOR); 
         gc.fillRectangle(pitch_pos.x + 1, pitch_pos.y + 1, pitch_size.x - 2, pitch_size.y / 2 - 2);
         gc.fillRectangle(pitch_pos.x + 1, pitch_pos.y + pitch_size.y / 2 + 1, pitch_size.x - 2, pitch_size.y / 2 - 3);		
         draw_player(gc, pitch.player_1);
         draw_player(gc, pitch.player_2);
         draw_ball(gc);		*/
    }

    public void drawPitch(GC gc) {
        updatePitchPos();
        drawPitchBorders(gc);
        drawPlayer(gc, pitch.getPlayer(Side.HOME));
        drawPlayer(gc, pitch.getPlayer(Side.AWAY));
        drawBall(gc);
    }

    public void drawPitchBorders(GC gc) {
        gc.setBackground(PITCH_COLOR);
        gc.setLineWidth(2);
        Rectangle pitchRectangle = mapRect(0, Pitch.HALF_HEIGHT, Pitch.WIDTH, -Pitch.HALF_HEIGHT);
        gc.fillRectangle(pitchRectangle);
        gc.setBackground(DARK_PITCH_COLOR);

        if (DRAW_BLOCKED_ZONES) {
            double homePlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.HOME));
            gc.fillRectangle(mapRect(0, 0, Pitch.WIDTH, -homePlayerBlockedZoneLength));
            double awayPlayerBlockedZoneLength = pitch.calculateNetBlockedZoneLength(pitch.getPlayer(Side.AWAY));
            gc.fillRectangle(mapRect(0, awayPlayerBlockedZoneLength, Pitch.WIDTH, 0));
        }

        gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_RED));
        gc.drawRectangle(pitchRectangle);
        gc.drawLine(pitchPos.x, pitchPos.y + pitchSize.y / 2, pitchPos.x + pitchSize.x, pitchPos.y + pitchSize.y / 2);
    }

    public void drawPlayer(GC gc, Player p) {
        double coef = getPitchSizeCoef();
        int radius = (int) (coef * PLAYER_SIZE / 2);
        if (radius < 1) {
            radius = 1;
        }
        Point pos = mapPos(p.getPosition());
        if (!p.isLying()) {
            gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_BLUE));
            gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_BLUE));
        } else {
            gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_DARK_RED));
            gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_RED));
        }
        gc.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
        gc.setLineWidth(2);
        gc.drawOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
    }

    public void drawBall(GC gc) {
        Ball ball = pitch.getBall();
        Point pos = mapPos(ball.getPosition());
        double coef = getPitchSizeCoef();
        int radius = (int) (coef * BALL_SIZE / 2);
        if (radius < 1) {
            radius = 1;
        }
        gc.setBackground(TenisMasters.display.getSystemColor(SWT.COLOR_BLACK));
        gc.setForeground(TenisMasters.display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);

        pos = mapPos(ball.getRealTarget());
        int size = (int) (coef * TARGET_SIZE / 2);
        if (size < 1) {
            size = 1;
        }
        gc.drawOval(pos.x - size, pos.y - size, size * 2, size * 2);

        pos = mapPos(ball.getVisibleTarget());
        size = (int) (coef * VISIBLE_TARGET_SIZE / 2);
        if (size < 1) {
            size = 1;
        }
        gc.setLineWidth(1);
        gc.drawLine(pos.x - size, pos.y - size, pos.x + size, pos.y + size);
        gc.drawLine(pos.x - size, pos.y + size, pos.x + size, pos.y - size);
    }
    
    public void hideResultCaption() {
        topLabel.setVisible(false);
        bottomLabel.setVisible(false);
    }
}
