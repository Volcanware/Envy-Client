package mathax.client.utils.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import mathax.client.MatHax;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistUtils {
    private static final Map<String, AudioPlaylist> playlists = new HashMap<>();

    private static final Path folderPath = MatHax.FOLDER.toPath().resolve("Music");
    private static final Path filePath = MatHax.MUSIC_FOLDER.toPath().resolve("Playlists.txt");

    public static void load() {
        if (!Files.exists(folderPath)) {
            File musicFolder = new File(MatHax.FOLDER, "Music");
            musicFolder.mkdirs();
        }

        playlists.clear();
        if (!Files.exists(filePath)) {
            playlists.put("https://music.youtube.com/playlist?list=PL63ZO-jXFTasqvj7WdEFQ6QtG6UBrl9CR", null); // Electronic Gems
            playlists.put("https://music.youtube.com/watch?v=BAPRv3Zts_w&list=RDQMuICnGifx4w8", null); // Chill Nation Mix
            playlists.put("https://music.youtube.com/playlist?list=PLRBp0Fe2GpglKIXdvLnzcnCdRwEr3tbkO", null); // NCS
            playlists.put("https://music.youtube.com/playlist?list=PL2vYabJDBczNGOYVGXMIlH4G_wcYje5Oi", null); // Rap Nation
            playlists.put("https://music.youtube.com/playlist?list=PLC1og_v3eb4h1MA88R3JHcPoLUpCRtxiZ", null); // Trap Nation
            playlists.put("https://music.youtube.com/playlist?list=PLU_bQfSFrM2PemIeyVUSjZjJhm6G7auOY", null); // Trap City
            save();
            playlists.clear();
        }

        try {
            Files.lines(filePath).forEach(s -> {
                if (s != null) SearchUtils.search(s, playlist -> playlists.put(s, playlist));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        try {
            Files.write(filePath, playlists.keySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        try {
            Files.delete(filePath);
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(String source) {
        playlists.put(source, null);
        save();
        load();
    }

    public static void remove(String source) {
        playlists.remove(source);
        save();
    }

    public static int count() {
        return playlists.size();
    }

    public static List<Map.Entry<String, AudioPlaylist>> getEntriesOrdered() {
        List<Map.Entry<String, AudioPlaylist>> l = new ArrayList<>(playlists.entrySet());
        l.sort((e1, e2) -> {
            int res = String.CASE_INSENSITIVE_ORDER.compare(e1.getValue().getName(), e2.getValue().getName());
            if (res == 0) res = e1.getValue().getName().compareTo(e2.getValue().getName());
            return res;
        });

        return l;
    }
}
