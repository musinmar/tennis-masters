package tm.lib.domain;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GameWorld
{
    private List<Season> seasons;
    private List<Person> players;

    public GameWorld()
    {
        players = new ArrayList<Person>();
        init();

        seasons = new ArrayList<Season>();
        seasons.add(new Season(this, 0));
    }

    public List<Season> getSeasons()
    {
        return seasons;
    }

    public Season getCurrentSeason()
    {
        return seasons.get(seasons.size() - 1);
    }

    public List<Person> getPlayers()
    {
        return players;
    }

    private void init()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse("players.xml");
            NodeList playerNodes = document.getElementsByTagName("player");
            for (int i = 0; i < playerNodes.getLength(); ++i)
            {
                Element playerElement = (Element) playerNodes.item(i);
                Person person = new Person();
                person.init(playerElement);
                players.add(person);
            }

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
