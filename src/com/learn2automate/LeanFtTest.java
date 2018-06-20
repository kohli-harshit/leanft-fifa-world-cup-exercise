package com.learn2automate;

import static org.junit.Assert.*;

import com.hp.lft.sdk.insight.InsightDescription;
import com.hp.lft.sdk.insight.InsightObject;
import com.hp.lft.sdk.web.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.hp.lft.sdk.*;
import com.hp.lft.verifications.*;

import unittesting.*;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LeanFtTest extends UnitTestClassBase {

    Browser browser;
    String websiteURL="https://www.fifa.com/worldcup";
    LinkDescription groupTableDescription = new LinkDescription.Builder()
        .tagName("A")
		.innerText(new RegExpProperty("Group .*")).build();

    WebElementDescription teamDescription =  new WebElementDescription.Builder()
        .className("fi-t fi-i--3 has-points")
		.tagName("DIV").build();

    WebElementDescription facebookLink = new WebElementDescription.Builder()
        .tagName("use")
		.outerHTML(new RegExpProperty(".*facebook.*"))
        .visible(true)
		.index(0).build();

    public LeanFtTest() {
        //Change this constructor to private if you supply your own public constructor
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        instance = new LeanFtTest();
        globalSetup(LeanFtTest.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        globalTearDown();
    }

    @Before
    public void setUp() throws Exception {
        browser = BrowserFactory.launch(BrowserType.CHROME);
        browser.navigate(websiteURL);
    }

    @After
    public void tearDown() throws Exception {
        browser.close();
    }

    @Test
    public void VerifyTables() throws Exception {
        String[] expectedGroups = new String[]{"Group A","Group B","Group C","Group D","Group E","Group F","Group G","Group H"};
        Set<String> expectedGroupSet = new HashSet<>(Arrays.asList(expectedGroups));
        Link[] groupLinks = browser.findChildren(Link.class,groupTableDescription);

        //Check count
        assertEquals(8,groupLinks.length);

        //Check titles
        for(Link groupLink:groupLinks){
            groupLink.highlight();
            String title = groupLink.getTitle();
            assertTrue(title + " is incorrect.",expectedGroupSet.contains(title));
            String teamOnTop = groupLink.findChildren(WebElement.class,teamDescription)[0].getOuterText();
            System.out.println("Team on Top of " + title + " is " + teamOnTop);
        }
    }

    @Test
    public void VerifyLogo() throws Exception{
        File logoFile = new File("resources\\logo.png");
        RenderedImage logoImage = ImageIO.read(logoFile);
        InsightObject logo = browser.describe(InsightObject.class,new InsightDescription(logoImage));

        assertTrue("Logo should exist",logo.exists());
        logo.highlight();

    }

    @Test
    public void VerifyTwitter() throws Exception{
        WebElement facebook = browser.describe(WebElement.class,facebookLink);
        facebook.highlight();
        VisualRelation visualRelation = new VisualRelation();
        visualRelation.setTestObject(facebook);
        visualRelation.setProximityRelation(ProximityVisualRelation.CLOSEST_ON_X_AXIS);

        WebElement elementToFind = browser.describe(WebElement.class,new WebElementDescription.Builder().tagName("use").vri(visualRelation).build());

        assertTrue("Icon to the right of facebook should exist",elementToFind.exists());
        elementToFind.highlight();
        assertTrue("Icon should be of Twitter",elementToFind.getOuterHTML().toLowerCase().contains("icon-tw-"));
    }

}