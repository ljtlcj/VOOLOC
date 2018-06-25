package test.xk_ys_VOOLOC.Net;


/**
 * Created by 柯东煜 on 2017/9/25.
 */

public class BaseNet implements Net,Runnable {
    protected NetCallback callback=new NetCallback() {
        @Override
        public void execute(String result) {

        }

        @Override
        public void error(String result) {

        }
    };
    @Override
    public void setCallback(NetCallback callback){
        this.callback=callback;
    }
    @Override
    public void run(){
    }
}
