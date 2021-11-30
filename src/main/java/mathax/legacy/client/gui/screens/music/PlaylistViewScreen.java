package mathax.legacy.client.gui.screens.music;

public class PlaylistViewScreen/* extends WindowScreen {
    private final AudioPlaylist results;
    private final MusicTab.MusicScreen musicScreen;
    private List<WMusicWidget> childWidgets;
    private WTable table;

    public PlaylistViewScreen(GuiTheme theme, AudioPlaylist playlist, MusicTab.MusicScreen musicScreen) {
        super(theme, "Music - " + playlist.getName());
        this.results = playlist;
        this.musicScreen = musicScreen;
        this.parent = musicScreen;
    }

    @Override
    public void initWidgets() {
        super.init();
        childWidgets = new ArrayList<>();
        WPaginationProvider pagination = new WPaginationProvider(j -> construct());
        childWidgets.add(new WPlaylistPage(pagination, results::getTracks, i -> {
            Music.trackScheduler.queue(results.getTracks().get(i));
        }, null));
        childWidgets.add(pagination);
        if (table != null) table.clear();
        clear();
        table = add(theme.table()).expandX().minWidth(300).widget();
        construct();
    }

    public void construct() {
        table.clear();
        table.add(theme.button("Add all")).expandX().widget().action = () -> {
            for (AudioTrack track : results.getTracks()) {
                Music.trackScheduler.queue(track);
            }

            mc.setScreen(parent);
            musicScreen.construct();
        };
        table.row();
        for (WMusicWidget wMusicWidget : childWidgets) {
            wMusicWidget.add(table, musicScreen, theme);
        }
    }

    public PlaylistViewScreen setParent(Screen screen) {
        parent = screen;
        return this;
    }

    public String getTitleString() {
        return title.getString();
    }
}
*/ {}
