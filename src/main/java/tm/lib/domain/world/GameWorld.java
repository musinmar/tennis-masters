package tm.lib.domain.world;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tm.lib.domain.core.Knight;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private List<Season> seasons = new ArrayList<Season>();
    private List<Knight> players = new ArrayList<Knight>();

    private EloRating eloRating;
    private NationRating nationRating;

    public GameWorld() {
        init();
        eloRating = new EloRating(players);
        nationRating = new NationRating();
        nationRating.initDefault();
        nationRating.calculateRankingsAndPrint();
        seasons.add(new Season(this, 0));
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public Season getCurrentSeason() {
        return seasons.get(seasons.size() - 1);
    }

    public List<Knight> getPlayers() {
        return players;
    }

    public EloRating getEloRating() {
        return eloRating;
    }

    public NationRating getNationRating() {
        return nationRating;
    }

    private void init() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse("players.xml");
            NodeList playerNodes = document.getElementsByTagName("player");
            for (int i = 0; i < playerNodes.getLength(); ++i) {
                Element playerElement = (Element) playerNodes.item(i);
                Knight knight = new Knight();
                knight.init(playerElement);
                players.add(knight);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
