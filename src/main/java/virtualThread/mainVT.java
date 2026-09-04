package virtualThread;

public class mainVT {
    public static void main(String[] args) throws InterruptedException {
        String path = "/Users/daniele/IdeaProjects";
        MonitorAccumulator res = new VtFSStatLib().getFSReport(path, 1700, 4);
        Thread.sleep(10);
        res.printReport();
        Thread.sleep(300);
        System.out.println(res.getReportAsString());
    }
}