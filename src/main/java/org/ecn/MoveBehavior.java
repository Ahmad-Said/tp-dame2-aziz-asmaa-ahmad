package org.ecn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MoveBehavior {
    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle postion souhaitee a partir du position actuelle
     * L'axe x a le sens dx du gauche a droit
     * 7 8 9
     * 4   6
     * 1 2 3
     */
    public static final Map<Integer, Integer> DIRECTION_TO_DX = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(1, -1);
        put(2, 0);
        put(3, 1);
        put(6, 1);
        put(9, 1);
        put(8, 0);
        put(7, -1);
        put(4, -1);
    }});

    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle postion souhaitee a partir du position actuelle
     * l'axe y a le sens du haut en bas
     * 7 8 9
     * 4   6
     * 1 2 3
     */
    public static final Map<Integer, Integer> DIRECTION_TO_DY = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {{
        put(1, 1);
        put(2, 1);
        put(3, 1);
        put(6, 0);
        put(9, -1);
        put(8, -1);
        put(7, -1);
        put(4, 0);
    }});
}
