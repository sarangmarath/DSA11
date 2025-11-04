import java.util.*;

public class WindowStats {
    
    private static class Event {
        long tMs;
        String user;
        Event(long t, String u) { this.tMs = t; this.user = u; }
    }

    private final long windowMs;
    private final Deque<Event> q;
    private final Map<String, Integer> userCount;
    private int totalEvents;

    public WindowStats(int WSeconds) {
        this.windowMs = WSeconds * 1000L;
        this.q = new ArrayDeque<>();
        this.userCount = new HashMap<>();
        this.totalEvents = 0;
    }

    public String ingest(long tMs, String userId) {
        // Add new event
        q.addLast(new Event(tMs, userId));
        userCount.put(userId, userCount.getOrDefault(userId, 0) + 1);
        totalEvents++;

        evictOld(tMs);
        
        int uniqueUsers = userCount.size();
        int qps = (int) (totalEvents / (windowMs / 1000));

        String result = uniqueUsers + " " + qps;
        System.out.println("(" + tMs + "," + userId + ") → " + result);
        return result;
    }

    private void evictOld(long tMs) {
        long threshold = tMs - windowMs;
        while (!q.isEmpty() && q.peekFirst().tMs <= threshold) {
            Event e = q.removeFirst();
            totalEvents--;
            int c = userCount.get(e.user);
            if (c == 1) userCount.remove(e.user);
            else userCount.put(e.user, c - 1);
        }
    }

    //print current queue state
    public void printQueueState() {
        System.out.print("Current Window: [");
        for (Event e : q) System.out.print("(" + e.tMs + "," + e.user + ") ");
        System.out.println("]");
    }

    // Test 
    public static void main(String[] args) {
        WindowStats ws = new WindowStats(5);

        ws.ingest(1000, "A");  
        ws.ingest(2500, "B");  
        ws.ingest(7000, "B");  
        ws.printQueueState();
    }
}
