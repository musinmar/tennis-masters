package tm.ui;

import tm.lib.domain.competition.Match;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.*;

public class MatchInfoWidget extends Composite
{
    Match match;
    Label p1_label;
    Label p2_label;
    public Label score_label;
    public Label time_label;
    Font font;

    public MatchInfoWidget(Composite parent, Match m)
    {
        super(parent, SWT.BORDER);
        match = m;

        /*RowLayout layout = new RowLayout(SWT.VERTICAL);
         layout.fill = true;*/
        GridLayout layout = new GridLayout();
        setLayout(layout);

        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        p1_label = new Label(this, SWT.CENTER);
        p1_label.setLayoutData(data);
        p2_label = new Label(this, SWT.CENTER);
        p2_label.setLayoutData(data);
        time_label = new Label(this, SWT.CENTER);
        time_label.setLayoutData(data);
        score_label = new Label(this, SWT.CENTER);
        score_label.setLayoutData(data);

        FontData[] font_data = p1_label.getFont().getFontData();
        font_data[0].setHeight(12);
        font = new Font(TenisMasters.display, font_data[0]);
        p1_label.setFont(font);
        p2_label.setFont(font);
        score_label.setFont(font);
        time_label.setFont(font);
        update();

        addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                font.dispose();
            }
        });
    }

    public void update()
    {
        p1_label.setText(match.getFirstPlayer().getShortName());
        p2_label.setText(match.getSecondPlayer().getShortName());
        score_label.setText("0:0/ 0:0/ д.в. 0:0");
        time_label.setText("00:00");
    }

    public void set_time(long time)
    {
        long secs = time / 1000;
        long mins = secs / 60;
        secs = secs % 60;

        String ss = String.valueOf(secs);
        if (ss.length() == 1)
        {
            ss = "0" + ss;
        }
        String sm = String.valueOf(mins);
        if (sm.length() == 1)
        {
            sm = "0" + sm;
        }
        String s = sm + ":" + ss;
        time_label.setText(s);
        time_label.pack();
        this.layout();
    }
}
