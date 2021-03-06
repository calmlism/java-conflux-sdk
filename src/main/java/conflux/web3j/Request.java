package conflux.web3j;

import java.io.IOException;
import java.util.Arrays;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Response;

public class Request<T, R extends Response<?> & HasValue<T>> extends org.web3j.protocol.core.Request<Object, R> {
	
	private int retry;
	private long intervalMillis;
	
	public Request() {
	}
	
	public Request(Web3jService service, String method, Class<R> responseType, Object... params) {
		super(method, Arrays.asList(params), service, responseType);
	}
	
	public Request<T, R> withRetry(int retry, long intervalMillis) {
		this.retry = retry;
		this.intervalMillis = intervalMillis;
		return this;
	}
	
	private T getResult(R response) throws RpcException {
		if (response.getError() != null) {
			throw new RpcException(response.getError());
		}
		
		return response.getValue();
	}
	
	public T sendAndGet() throws RpcException {
		return this.sendAndGet(this.retry);
	}
	
	public T sendAndGet(int retry) throws RpcException {
		return this.sendAndGet(retry, this.intervalMillis);
	}
	
	public T sendAndGet(int retry, long intervalMills) throws RpcException {
		R response = null;
		
		while (response == null) {
			try {
				response = this.send();
			} catch (IOException e) {
				if (retry <= 0) {
					throw RpcException.sendFailure(e);
				}
				
				retry--;
				
				if (intervalMills > 0) {
					try {
						Thread.sleep(intervalMills);
					} catch (InterruptedException e1) {
						throw RpcException.interrupted(e1);
					}
				}
			}
		}
		
		return this.getResult(response);
	}

}
