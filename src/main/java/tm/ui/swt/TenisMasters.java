package tm.ui.swt;

import org.eclipse.swt.widgets.*;

public class TenisMasters
{
    static MainWindow main_window;
    static public Display display;

    public static void main(String[] arg)
    {
        display = new Display();
        main_window = new MainWindow(display);
        main_window.shell.open();
        while (!main_window.shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        display.dispose();
    }
}
