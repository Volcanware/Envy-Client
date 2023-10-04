/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package mathax.client.utils.notebot.decoder;

import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.misc.Notebot;
import mathax.client.utils.notebot.song.Song;

import java.io.File;

public abstract class SongDecoder {
    protected Notebot notebot = Modules.get().get(Notebot.class);

    /**
     * Parse file to a {@link Song} object
     *
     * @param file Song file
     * @return A {@link Song} object
     */
    public abstract Song parse(File file) throws Exception;
}
