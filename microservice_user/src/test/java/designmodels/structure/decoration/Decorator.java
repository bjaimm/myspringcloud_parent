package designmodels.structure.decoration;

public abstract class Decorator implements Showable{

    private Showable showable;

    public Decorator(Showable showable){
        this.showable = showable;
    }

    @Override
    public void show() {

        showable.show();

    }

}
