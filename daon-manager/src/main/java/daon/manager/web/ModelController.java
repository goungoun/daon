package daon.manager.web;

import daon.core.result.ModelInfo;
import daon.core.util.ModelUtils;
import daon.manager.model.param.ModelParams;
import daon.manager.service.ModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by mac on 2017. 3. 8..
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/model")
public class ModelController {

	@Autowired
	private ModelService modelService;

	/**
	 * 모델 적용
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/apply", method = RequestMethod.GET)
	public boolean apply(String seq) throws Exception {

		ModelInfo modelInfo = modelService.modelInfo(seq);

		ModelUtils.setModel(modelInfo);

		return modelInfo.isSuccess();
	}

	/**
	 * 모델 검색
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestBody ModelParams params) throws Exception {

		log.info("params : {}, {}, {}", params, params.getFrom(), params.getSize());

		return modelService.search(params);
	}

	/**
	 * 모델 base URL 가져오기
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/baseURL", method = RequestMethod.GET)
	public String search(HttpServletRequest request) throws Exception {

        String url = request.getRequestURL().toString();
        String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath();

		log.info("baseUri : {}", baseURL);
		return baseURL;
	}

	/**
	 * 모델 다운로드
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<Resource> download(String seq) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		//파일 네이밍 정의 필요
		headers.add("Content-disposition", "attachment;filename=model_" + seq + ".dat");

		byte[] data = modelService.model(seq);
		ByteArrayResource resource = new ByteArrayResource(data);

		return ResponseEntity.ok()
				.headers(headers)
				.contentLength(data.length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(resource);
	}

}