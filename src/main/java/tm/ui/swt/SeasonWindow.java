package tm.ui.swt;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import tm.lib.domain.competition.Competition;
import tm.lib.domain.competition.Match;
import tm.lib.domain.competition.MultiStageCompetition;
import tm.lib.domain.core.MatchScore;
import tm.lib.domain.world.Season;
import tm.lib.engine.MatchSimulator;

public class SeasonWindow
{
    private Shell shell;
    private Season season;
    private Text output;
    private Font outputFont;

    private Combo tournamentCombo;
    private Combo stageCombo;
    private Combo subStageCombo;

    SeasonWindow(Shell parent, Season watchedSeason)
    {
        this.season = watchedSeason;

        shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.RESIZE | SWT.MIN | SWT.MAX);

        GridLayout mainLayout = new GridLayout();
        shell.setLayout(mainLayout);

        Composite comboBoxComposite = createComboBoxes();
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        comboBoxComposite.setLayoutData(data);

        output = new Text(shell, SWT.MULTI | SWT.LEFT | SWT.V_SCROLL);
        data = new GridData();
        data.widthHint = 300;
        data.heightHint = 300;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        output.setText("Abc");
        outputFont = new Font(shell.getDisplay(), "Courier", 11, SWT.NONE);
        output.setFont(outputFont);
        output.setLayoutData(data);

        Button nextButton = new Button(shell, SWT.PUSH);
        nextButton.setText("Следующий матч");
        data = new GridData();
        data.verticalAlignment = GridData.END;
        nextButton.setLayoutData(data);
        nextButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Match match = season.getNextMatch();
                MatchWindow match_window = new MatchWindow(shell, match);
                match_window.shell.setMaximized(true);
                MatchScore score = match_window.open();
                if (score != null)
                {
                    season.processMatch(match, score);
                    updateText();
                }
            }
        });
        
        Button nextFastButton = new Button(shell, SWT.PUSH);
        nextFastButton.setText("Следующий матч (быстро)");
        data = new GridData();
        data.verticalAlignment = GridData.END;
        nextFastButton.setLayoutData(data);
        nextFastButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Match match = season.getNextMatch();
                MatchSimulator matchSimulator = new MatchSimulator(match);
                MatchSimulator.State state;
                do
                {
                    state = matchSimulator.proceed();
                } while (state != MatchSimulator.State.MATCH_ENDED);
                MatchScore score = matchSimulator.getCurrentScore();
                if (score != null)
                {
                    season.processMatch(match, score);
                    updateText();
                }
            }
        });

        shell.pack();
        updateText();
    }

    final Composite createComboBoxes()
    {
        Composite composite = new Composite(shell, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        composite.setLayout(gridLayout);

        Combo combo0 = new Combo(composite, SWT.READ_ONLY);
        combo0.setItems(new String[]
        {
            "Item 1", "Item 2", "Item 2"
        });
        combo0.setText("combo0");
        GridData data = new GridData();
        data.widthHint = 150;
        data.grabExcessHorizontalSpace = true;
        combo0.setLayoutData(data);

        stageCombo = new Combo(composite, SWT.READ_ONLY);
        stageCombo.setText("combo2");
        data = new GridData();
        data.widthHint = 150;
        data.grabExcessHorizontalSpace = true;
        stageCombo.setLayoutData(data);
        stageCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                fillSubStageCombo();
                updateText();
            }
        });

        tournamentCombo = new Combo(composite, SWT.READ_ONLY);
        data = new GridData();
        data.widthHint = 150;
        data.grabExcessHorizontalSpace = true;
        tournamentCombo.setLayoutData(data);
        tournamentCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                fillStageCombo();
                fillSubStageCombo();
                updateText();
            }
        });

        subStageCombo = new Combo(composite, SWT.READ_ONLY);
        subStageCombo.setText("combo3");
        data = new GridData();
        data.widthHint = 150;
        data.grabExcessHorizontalSpace = true;
        subStageCombo.setLayoutData(data);
        subStageCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateText();
            }
        });

        fillTournamentCombo();
        fillStageCombo();
        fillSubStageCombo();

        return composite;
    }

    private void fillTournamentCombo()
    {
        MultiStageCompetition seasonCompetition = (MultiStageCompetition) season.getSeasonCompetition();
        Competition[] tournaments = seasonCompetition.getStages();
        String[] comboItems = new String[tournaments.length + 1];
        comboItems[0] = "Все";
        int index = 1;
        for (Competition tournament : tournaments)
        {
            comboItems[index++] = tournament.getName();
        }
        tournamentCombo.setItems(comboItems);
        tournamentCombo.select(0);
    }

    private void fillStageCombo()
    {
        if (tournamentCombo.getSelectionIndex() <= 0)
        {
            stageCombo.setEnabled(false);
            stageCombo.deselectAll();
            //stageCombo.select(0);
            return;
        }

        stageCombo.setEnabled(true);
        MultiStageCompetition seasonCompetition = (MultiStageCompetition) season.getSeasonCompetition();
        Competition[] tournaments = seasonCompetition.getStages();
        Competition tournament = tournaments[tournamentCombo.getSelectionIndex() - 1];
        Competition[] stages = ((MultiStageCompetition) tournament).getStages();

        String[] comboItems = new String[stages.length + 1];
        comboItems[0] = "Все";
        int index = 1;
        for (Competition stage : stages)
        {
            comboItems[index++] = stage.getName();
        }
        stageCombo.setItems(comboItems);
        stageCombo.select(0);
    }

    private void fillSubStageCombo()
    {
        if (stageCombo.getSelectionIndex() <= 0)
        {
            subStageCombo.setEnabled(false);
            subStageCombo.deselectAll();
            //subStageCombo.select(0);
            return;
        }

        subStageCombo.setEnabled(true);
        MultiStageCompetition seasonCompetition = (MultiStageCompetition) season.getSeasonCompetition();
        Competition[] tournaments = seasonCompetition.getStages();
        Competition tournament = tournaments[tournamentCombo.getSelectionIndex() - 1];
        Competition[] stages = ((MultiStageCompetition) tournament).getStages();
        Competition stage = stages[stageCombo.getSelectionIndex() - 1];
        Competition[] subStages = ((MultiStageCompetition) stage).getStages();

        String[] comboItems = new String[subStages.length + 1];
        comboItems[0] = "Все";
        int index = 1;
        for (Competition subStage : subStages)
        {
            comboItems[index++] = subStage.getName();
        }
        subStageCombo.setItems(comboItems);
        subStageCombo.select(0);
    }

    private void updateText()
    {
        Competition competitionToPrint;

        if (tournamentCombo.getSelectionIndex() <= 0)
        {
            competitionToPrint = season.getSeasonCompetition();
        }
        else
        {
            Competition[] tournaments = ((MultiStageCompetition) season.getSeasonCompetition()).getStages();
            Competition tournament = tournaments[tournamentCombo.getSelectionIndex() - 1];
            if (stageCombo.getSelectionIndex() <= 0)
            {
                competitionToPrint = tournament;
            }
            else
            {
                Competition[] stages = ((MultiStageCompetition) tournament).getStages();
                Competition stage = stages[stageCombo.getSelectionIndex() - 1];
                if (subStageCombo.getSelectionIndex() <= 0)
                {
                    competitionToPrint = stage;
                }
                else
                {
                    Competition[] subStages = ((MultiStageCompetition) stage).getStages();
                    competitionToPrint = subStages[subStageCombo.getSelectionIndex() - 1];
                }
            }
        }

        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        competitionToPrint.print(printStream);
        output.setText(outputStream.toString());
    }

    public void open()
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
        outputFont.dispose();
    }
}
