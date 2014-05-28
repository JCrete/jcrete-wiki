/*
 * @(#)CreatePagesForSchedule.java
 * ===========================================================================
 * (C) Copyright MEKO-S GmbH 2001, 2014
 * ===========================================================================
 * Created on 28.05.2014
 */

package org.jcrete.wiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.wikipedia.Wiki;

/**
 * @author rgra
 *
 */
public class CreatePagesForSchedule {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private static final List<String> IGNORE_PAGE_LIST = Arrays.asList(
            "JCrete2014:Breaking News", "JCrete2014:Hackathon",
            "JCrete2014:Outputs");

    private final int year;
    private final String pageNamespace;

    public CreatePagesForSchedule(int year) {
        this.year = year;
        this.pageNamespace = "JCrete" + year + ":";
    }

    private void run(final String user, final String password)
            throws IOException, LoginException {
        Wiki wiki = login(user, password);

        List<String> nonExistentPages = findNonExistentPagesInSchedule(wiki);

        for (String newPage : nonExistentPages) {
            String title = newPage.substring(pageNamespace.length());
            String convenor = "convenor";
            String text = createTemplatePageText(title, convenor);
            String summary = "JCrete " + year + " Schedule: " + title;
            wiki.edit(newPage, text, summary);
        }
    }

    private String createTemplatePageText(String title, String convenor) {
        StringBuilder b = new StringBuilder();
        b.append("{{MyTitle|" + title + "}}").append(LINE_SEP);
        b.append("{{jcrete-report-" + year + "           ").append(LINE_SEP);
        b.append("| title = " + title + "            ").append(LINE_SEP);
        b.append("| convenor = " + convenor + "      ").append(LINE_SEP);
        b.append("| participants =                   ").append(LINE_SEP);
        b.append("                                   ").append(LINE_SEP);
        b.append("* List                             ").append(LINE_SEP);
        b.append("* them                             ").append(LINE_SEP);
        b.append("* here                             ").append(LINE_SEP);
        b.append("* ...                              ").append(LINE_SEP);
        b.append("                                   ").append(LINE_SEP);
        b.append("|summary =                         ").append(LINE_SEP);
        b.append("                                   ").append(LINE_SEP);
        b.append("Summary of the discussion goes here ...   ").append(LINE_SEP);
        b.append("                                    ").append(LINE_SEP);
        b.append("| recommendations =                 ").append(LINE_SEP);
        b.append("                                    ").append(LINE_SEP);
        b.append("Recommendations go here             ").append(LINE_SEP);
        b.append("                                    ").append(LINE_SEP);
        b.append("* ...                               ").append(LINE_SEP);
        b.append("* ...                               ").append(LINE_SEP);
        b.append("                                    ").append(LINE_SEP);
        b.append("}}                                  ").append(LINE_SEP);
        b.append(LINE_SEP);
        return b.toString();
    }

    private List<String> findNonExistentPagesInSchedule(Wiki wiki)
            throws IOException {

        List<String> nonExistentPages = new ArrayList<>();
        String[] links = wiki.getLinksOnPage(pageNamespace + "Schedule", 100);
        boolean[] existingCheck = wiki.exists(links);
        for (int i = 0; i < links.length; i++) {
            String link = links[i];
            boolean exists = existingCheck[i];

            if (!exists && link.startsWith(pageNamespace)) {
                nonExistentPages.add(link);
            }
        }
        nonExistentPages.removeAll(IGNORE_PAGE_LIST);
        return nonExistentPages;
    }

    private Wiki login(String user, String password)
            throws FailedLoginException, IOException {
        Wiki wiki = new Wiki("wikieducator.org", "");
        wiki.setThrottle(5000); // set the edit throttle to 0.2 Hz
        wiki.login(user, password);
        return wiki;
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        CreatePagesForSchedule creator = new CreatePagesForSchedule(2014);
        try {
            creator.run(username, password);
        } catch (LoginException | IOException e) {
            e.printStackTrace();
        }
    }

}
