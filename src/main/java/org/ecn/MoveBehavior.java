package org.ecn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveBehavior {
    private MoveBehavior() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle position souhaitee a partir du position actuelle
     * L'axe x a le sens dx du gauche a droit
     * 7 8 9
     * 4   6
     * 1 2 3
     */
    public static final Map<Integer, Integer> DIRECTION_TO_DX = Collections.unmodifiableMap(createDXDirectionMap());

    private static Map<Integer, Integer> createDXDirectionMap() {
        Map<Integer, Integer> dxMap = new HashMap<>();
        dxMap.put(1, -1);
        dxMap.put(2, 0);
        dxMap.put(3, 1);
        dxMap.put(6, 1);
        dxMap.put(9, 1);
        dxMap.put(8, 0);
        dxMap.put(7, -1);
        dxMap.put(4, -1);
        dxMap.put(5, 0);
        return dxMap;
    }

    /**
     * Utilise pour traduire le sens de 1 a 9 sauf le 5 dans un carre par rapport a son centre,
     * ou le numero designe la nouvelle position souhaitee a partir du position actuelle
     * l'axe y a le sens du haut en bas
     * 7 8 9
     * 4   6
     * 1 2 3
     */
    public static final Map<Integer, Integer> DIRECTION_TO_DY = Collections.unmodifiableMap(createDYDirectionMap());

    private static Map<Integer, Integer> createDYDirectionMap() {
        Map<Integer, Integer> dxMap = new HashMap<>();
        dxMap.put(1, 1);
        dxMap.put(2, 1);
        dxMap.put(3, 1);
        dxMap.put(6, 0);
        dxMap.put(9, -1);
        dxMap.put(8, -1);
        dxMap.put(7, -1);
        dxMap.put(4, 0);
        dxMap.put(5, 0);
        return dxMap;
    }

    public static int getDirectionFromDxDy(int dx, int dy) {
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
