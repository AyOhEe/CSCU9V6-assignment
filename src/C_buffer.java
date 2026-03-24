import java.util.*;

public class C_buffer {
	
    private Vector<CoordinatorRequest> data;
    
    public C_buffer (){
    	data = new Vector<>();
    }    

    public int size(){
    	return data.size();
    }

    public synchronized void saveRequest (CoordinatorRequest r){
    	data.add(r);
    }

    public void show(){
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
		System.out.println(" ");
    }
    
    public void add(CoordinatorRequest o){
    	data.add(o);
    }

    synchronized public CoordinatorRequest  get(){
    	CoordinatorRequest request = null;
		if (!data.isEmpty()){
		    request = data.removeFirst();
		}

		return request;
    }
}
