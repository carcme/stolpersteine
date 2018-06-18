package me.carc.stolpersteine.data.remote.bio;

import com.fcannizzaro.jsoup.annotations.interfaces.AfterBind;
import com.fcannizzaro.jsoup.annotations.interfaces.Selector;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco Cannizzaro (fcannizzaro)
 */

public class ImageParser {
    private static final String TAG = ImageParser.class.getName();

    private List<BioImages> bioImagesList = new ArrayList<>();

    public List<BioImages> getBioImagesList() {
        return bioImagesList;
    }

    @Selector(".rsDefault")
    public void imagesExtractor(Element imageElement) {

        for(Element element : imageElement.children()) {
            List<Node> nodes = element.childNodes();
            for (Node node : nodes) {
                if(node instanceof Element && ((Element) node).tagName().equalsIgnoreCase("a")) {
                    BioImages image = new BioImages();
                    image.setTitle(((Element)node).getAllElements().eachAttr("title").get(0));
                    image.setPublicImage(((Element)node).getAllElements().eachAttr("href").get(0));
                    image.setBigImage(((Element)node).getAllElements().eachAttr("data-rsbigimg").get(0));
                    image.setThumbnail(((Element)node).getAllElements().eachAttr("data-rstmb").get(0));
                    bioImagesList.add(image);
                    break;
                }
            }

        }
    }

    @AfterBind
    void attached() {
    }
}
