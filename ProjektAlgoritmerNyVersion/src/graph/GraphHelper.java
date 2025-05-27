package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphHelper {
	public static <T> List<UpdatedPosition<T>> moveLocation(List<ServerHall<T>> original) {
        Random rand = new Random();
        List<UpdatedPosition<T>> updated = new ArrayList<>();

        for (ServerHall<T> hall : original) {
            double newX = hall.getX() + rand.nextDouble() * 20 - 10; // Shift between -10 and +10
            double newY = hall.getY() + rand.nextDouble() * 20 - 10;
            updated.add(new UpdatedPosition<>(newX, newY, hall.getInfo()));
        }

        return updated;
    }
	
}
