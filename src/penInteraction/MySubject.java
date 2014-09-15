package penInteraction;

public interface MySubject {
	public void attach(MyObserver observer);

	public void detach(MyObserver observer);

	void notifyObservers();
}
