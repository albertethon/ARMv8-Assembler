package Test;


public class Test_exception {
    public static void main(String[] args) {
        int i,j,temp;
        i = 0;
        j = 1;
        temp = 0;
        try{
            if (i==0)throw new RuntimeException("i==8");
            if (j==1)throw new RuntimeException("j==1");
            if (temp==0)throw new RuntimeException("temp == 0");
        }catch (RuntimeException message){
            System.out.println(message.getMessage());
        }
    }
}