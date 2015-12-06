package tm.ui;

import tm.lib.domain.GameWorld;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainWindow
{
    public Shell shell;
    public GameWorld world;

    public MainWindow(Display parent)
    {
        world = new GameWorld();

        shell = new Shell(parent);
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        layout.pack = false;
        layout.marginLeft = 20;
        layout.marginRight = 20;
        layout.marginTop = 20;
        layout.marginBottom = 20;
        layout.spacing = 10;
        shell.setLayout(layout);

        RowData rd = new RowData(120, 50);
        Button next_button = new Button(shell, SWT.PUSH);
        next_button.setText("Следующий матч");
        next_button.setLayoutData(rd);
        next_button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                SeasonWindow seasonWindow = new SeasonWindow(shell, world.getCurrentSeason());
                seasonWindow.open();
            }
        });

        Button close_button = new Button(shell, SWT.PUSH);
        close_button.setText("Выход");
        close_button.setLayoutData(rd);
        close_button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        shell.pack();
    }
}
