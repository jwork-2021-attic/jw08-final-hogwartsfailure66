package asciiPanel;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class AsciiPanelTest {
    AsciiPanel asciiPanel;

    @Before
    public void setAsciiPanel() {
        asciiPanel = new AsciiPanel();
    }

    @Test
    public void getCharHeight() {
        assertEquals(16, asciiPanel.getCharHeight());
    }

    @Test
    public void getCharWidth() {
        assertEquals(9, asciiPanel.getCharWidth());
    }

    @Test
    public void getHeightInCharacters() {
        assertEquals(24, asciiPanel.getHeightInCharacters());
    }

    @Test
    public void getWidthInCharacters() {
        assertEquals(80, asciiPanel.getWidthInCharacters());
    }

    @Test
    public void getCursorX() {
        assertEquals(0, asciiPanel.getCursorX());
    }

    @Test
    public void setCursorX() {
        asciiPanel.setCursorX(10);
        assertEquals(10, asciiPanel.getCursorX());
    }

    @Test
    public void getCursorY() {
        assertEquals(0, asciiPanel.getCursorY());
    }

    @Test
    public void setCursorY() {
        asciiPanel.setCursorY(10);
        assertEquals(10, asciiPanel.getCursorY());
    }

    @Test
    public void setCursorPosition() {
        asciiPanel.setCursorPosition(20, 20);
        assertEquals(20, asciiPanel.getCursorX());
    }

    @Test
    public void getDefaultBackgroundColor() {
        assertEquals(Color.black, asciiPanel.getDefaultBackgroundColor());
    }

    @Test
    public void setDefaultBackgroundColor() {
        asciiPanel.setDefaultBackgroundColor(Color.blue);
        assertEquals(Color.BLUE, asciiPanel.getDefaultBackgroundColor());
    }

    @Test
    public void getDefaultForegroundColor() {
        assertEquals(new Color(192, 192, 192), asciiPanel.getDefaultForegroundColor());
    }

    @Test
    public void setDefaultForegroundColor() {
        asciiPanel.setDefaultForegroundColor(Color.cyan);
        assertEquals(Color.cyan, asciiPanel.getDefaultForegroundColor());
    }
}