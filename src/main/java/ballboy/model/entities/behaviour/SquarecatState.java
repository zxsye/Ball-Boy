package ballboy.model.entities.behaviour;

public enum SquarecatState {
    A, B, C, D;

    public SquarecatState next() {
        switch(this) {
            case A: return B;
            case B: return C;
            case C: return D;
            case D: return A;
        }
        return null; // default
    }
}
