package tako;

import storage.Storage;
import ui.Ui;

public class Tako {

    private Storage storage;
    public void run() {
        Ui.greet();
    }

    public Tako(String filepath) {
        storage = new Storage(filepath);
        storage.load();
    }

    public static void main(String[] args) {
        new Tako("data/Tako.txt").run();
    }
}
