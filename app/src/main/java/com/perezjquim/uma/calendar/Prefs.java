package com.perezjquim.uma.calendar;

public enum Prefs
{
    FILE_MISC
    {
        @Override
        public String toString() { return "misc"; }
    },
    KEY_AULAS
    {
        @Override
        public String toString() { return "aulas"; }
    },
    KEY_AVALIACOES
    {
        @Override
        public String toString() { return "avaliacoes"; }
    },
    KEY_LASTNR
    {
        @Override
        public String toString() { return "lastnr"; }
    }
}
