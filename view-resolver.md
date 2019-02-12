## mvc:view-resolvers

```
  <mvc:view-resolvers>
    <mvc:bean-name />
    <mvc:tiles view-class="CustomizedTilesView"/>
    <mvc:jsp prefix="/WEB-INF/views/" />
  </mvc:view-resolvers>


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomizedTilesView extends org.springframework.web.servlet.view.tiles3.TilesView {

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
		// TODO do something before rendering
		super.render(model, request, response);
	}

}

```
