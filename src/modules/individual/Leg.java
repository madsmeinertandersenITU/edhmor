package modules.individual;

public class Leg {
    public Module topPart;
    public Module bottomPart;

    public Leg(Module tModule, Module bModule) {
        this.topPart = tModule;
        this.bottomPart = bModule;
    }
}
