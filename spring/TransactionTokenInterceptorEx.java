import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.terasoluna.gfw.web.token.transaction.TransactionToken;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenContextImpl;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenInfo;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenInterceptor;
import org.terasoluna.gfw.web.token.transaction.TransactionTokenType;

public class TransactionTokenInterceptorEx extends TransactionTokenInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		boolean result = super.preHandle(request, response, handler);

		TransactionTokenContextImpl tokenContext = (TransactionTokenContextImpl) request
		        .getAttribute(TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME);
		TransactionTokenInfo tokenInfo = tokenContext.getTokenInfo();
		if (tokenInfo.getTransactionTokenType() == TransactionTokenType.NONE) {
			tokenContext = new TransactionTokenContextImpl(
			        new TransactionTokenInfoEx(tokenInfo.getTokenName(), tokenInfo.getTransactionTokenType()),
			        createReceivedToken(request));
			request.setAttribute(TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME, tokenContext);
		}
		return result;
	}

	TransactionToken createReceivedToken(final HttpServletRequest request) {
		String tokenStr = request.getParameter(TOKEN_REQUEST_PARAMETER);
		TransactionToken currentToken = new TransactionToken(tokenStr);
		return currentToken;
	}

	public class TransactionTokenInfoEx extends TransactionTokenInfo {

		public TransactionTokenInfoEx(String tokenName, TransactionTokenType tokenType) {
			super(tokenName, tokenType);
		}

		@Override
		public boolean needKeep() {
			return true;
		}

	}

}
