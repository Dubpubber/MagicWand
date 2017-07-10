/*
 * MIT License
 *
 * Copyright (c) 2017 Tyler Crowe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.loneboat.dnd.magicwand;

/*
 * com.loneboat.dnd.magicwand.Spell in MagicWand
 * Created by Tyler Crowe on 7/9/2017.
 * Website: https://loneboat.com
 * GitHub: https://github.com/Dubpubber
 * Machine Time: 1:55 AM
 */

import java.util.ArrayList;

/**
 * The spell object is used to populate the table view for MagicWand.
 */
public class Spell {

    // Access must be public for the cell factory to work.
    public String spellName;

    // Begin other spell properties.
    private ArrayList<Object> properties;

    /**
     * Constructor for the spell.
     * @param spellName - The name of the spell.
     */
    public Spell(String spellName) {
        this.spellName = spellName;
        this.properties = new ArrayList<>();
    }

    /**
     * Gets the spell's name as a readable string.
     * @return - The spell's name as a string
     */
    public String getSpellName() {
        return spellName;
    }
}
