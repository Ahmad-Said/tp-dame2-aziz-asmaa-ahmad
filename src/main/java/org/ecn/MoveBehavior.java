package org.ecn;

import java.util.*;
import java.util.stream.Collectors;

public class MoveBehavior {
    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle position souhaitee a partir du position actuelle
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
        put(5, 0);
    }});

    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle position souhaitee a partir du position actuelle
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
        put(5, 0);
    }});

    public static int getDirectionFromDxDy(int dx, int dy) {
        HashSet<Integer> directions = new HashSet<>();
        int finalDx = Integer.compare(dx, 0);
        int finalDy = Integer.compare(dy, 0);
        // collect possible direction from each d movement then return intersection
        Set<Integer> possibleDxDirection = DIRECTION_TO_DX.entrySet().stream().filter(mapEntry -> mapEntry.getValue() == finalDx)
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        Set<Integer> possibleDyDirection = DIRECTION_TO_DY.entrySet().stream().filter(mapEntry -> mapEntry.getValue() == finalDy)
                .map(Map.Entry::getKey).collect(Collectors.toSet());
        return possibleDxDirection.stream().filter(possibleDyDirection::contains).findFirst().orElse(-1);
    }
}
