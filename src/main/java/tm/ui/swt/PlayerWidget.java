package tm.ui.swt;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import tm.lib.engine.*;

public class PlayerWidget extends Composite
{
    Player player;
    Label name_label;
    ProgressBar energy_bar;
    Font font;
    Font font2;

    public PlayerWidget(Composite parent, Player p)
    {
        super(parent, SWT.BORDER);
        player = p;

        /*RowLayout layout = new RowLayout(SWT.HORIZONTAL);
         layout.pack = false;*/
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 15;
        this.setLayout(layout);

        name_label = new Label(this, SWT.NONE);
        FontData[] font_data = name_label.getFont().getFontData();
        font_data[0].setHeight(16);
        font = new Font(TenisMasters.display, font_data[0]);
        name_label.setFont(font);
        GridData data = new GridData();
        data.verticalAlignment = GridData.CENTER;

        Composite energy_widget = new Composite(this, SWT.NONE);
        RowLayout ew_layout = new RowLayout(SWT.HORIZONTAL);
        ew_layout.pack = false;
        ew_layout.spacing = 8;
        energy_widget.setLayout(ew_layout);
        data = new GridData();
        data.verticalAlignment = GridData.END;
        energy_widget.setLayoutData(data);

        Label energy_caption = new Label(energy_widget, SWT.NONE);
        energy_caption.setText("Состояние:");
        font_data = energy_caption.getFont().getFontData();
        font_data[0].setHeight(12);
        font2 = new Font(TenisMasters.display, font_data[0]);
        energy_caption.setFont(font2);
        energy_bar = new ProgressBar(energy_widget, SWT.HORIZONTAL);
        energy_bar.setMinimum(0);
        energy_bar.setMaximum(100);
        RowData rdata = new RowData();
        rdata.height = 5;
        rdata.width = 60;
        energy_bar.setLayoutData(rdata);

        update();

        addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                font.dispose();
                font2.dispose();
            }
        });
    }

    public void update()
    {
        name_label.setText(player.getPerson().getFullName() + " (" + player.getPerson().getNation() + ")");
        this.pack();
        energy_bar.setSelection((int) player.getEnergy());
    }
}
