package testcases;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@ToString
public class DelayTask<T> implements Delayed {

    private long delayTime;
    private long expireTime;
    private T taskName;

    public DelayTask(long delayTime,T taskName){
        this.delayTime=delayTime;
        this.expireTime=System.currentTimeMillis()+delayTime;
        this.taskName=taskName;
    }

    /**
     * 返回剩余时间=到期时间-系统时间
     * @param unit the time unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expireTime-System.currentTimeMillis(),unit);
    }

    @Override
    public int compareTo(Delayed o) {
        long compareResult= this.getDelay(TimeUnit.MILLISECONDS)-o.getDelay(TimeUnit.MILLISECONDS);
        return (int)compareResult;
    }
}
