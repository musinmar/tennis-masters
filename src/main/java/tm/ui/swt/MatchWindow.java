package tm.ui.swt;

import tm.lib.domain.core.MatchScore;
import tm.lib.domain.competition.base.MatchEvent;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import tm.lib.engine.Side;

public class MatchWindow
{
    public Shell shell;
    public Button start_button;
    Composite controls;
    Composite left_panel;
    UiMatchManager manager;
    public PlayerWidget player1_widget;
    public PlayerWidget player2_widget;
    public MatchInfoWidget match_info_widget;
    public PitchWidget pitch_widget;
    boolean started;
    public MatchScore final_score;

    MatchWindow(Shell parent, MatchEvent match)
    {
        manager = new UiMatchManager(this, match);
        started = false;
        final_score = null;

        shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.RESIZE | SWT.MIN | SWT.MAX);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 3;
        shell.setLayout(layout);

        player1_widget = new PlayerWidget(shell, manager.getPitch().getPlayer(Side.HOME));
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        player1_widget.setLayoutData(data);

        left_panel = new Composite(shell, SWT.BORDER);
        GridLayout left_panel_layout = new GridLayout();
        left_panel.setLayout(left_panel_layout);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.verticalSpan = 3;
        left_panel.setLayoutData(data);

        match_info_widget = new MatchInfoWidget(left_panel, manager.matchEvent);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        match_info_widget.setLayoutData(data);

        Composite composite1 = new Composite(left_panel, SWT.BORDER);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        composite1.setLayoutData(data);

        create_controls();

        pitch_widget = new PitchWidget(shell, manager.getPitch());
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        pitch_widget.setLayoutData(data);

        player2_widget = new PlayerWidget(shell, manager.getPitch().getPlayer(Side.AWAY));
        data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        player2_widget.setLayoutData(data);

        shell.pack();
		//pitch_widget.applyMatchResult();

        shell.addPaintListener(new PaintListener()
        {
            public void paintControl(PaintEvent e)
            {
                pitch_widget.redraw();
            }
        });
    }

    private void create_controls()
    {
        controls = new Composite(left_panel, SWT.BORDER);
        RowLayout controls_layout = new RowLayout(SWT.VERTICAL);
        controls_layout.pack = false;
        controls_layout.justify = true;
        controls.setLayout(controls_layout);
        RowData controls_rd = new RowData();
        controls_rd.width = 100;
        controls_rd.height = 30;

        start_button = new Button(controls, SWT.PUSH);
        start_button.setText("Начать");
        start_button.setLayoutData(controls_rd);
        start_button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!started)
                {
                    manager.start();
                    start_button.setText("Пауза");
                    started = true;
                }
                else
                {
                    manager.pause();
                    start_button.setText("Возобновить");
                    started = false;
                }
            }
        });

        Button close_button = new Button(controls, SWT.PUSH);
        close_button.setText("Закрыть");
        close_button.setLayoutData(controls_rd);
        close_button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                TenisMasters.display.timerExec(-1, manager.active_timer);
                shell.close();
                shell.dispose();
                manager.ui = null;
            }
        });
        controls.pack();
    }

    public MatchScore open()
    {
        shell.open();
        Display display = TenisMasters.display;
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return final_score;
    }

    public void refresh_pitch()
    {
        pitch_widget.update();
    }
}
