package edu.nchu.mall.services.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import edu.nchu.mall.components.feign.file.OSSFeignClient;
import edu.nchu.mall.models.callback.BrandCallback;
import edu.nchu.mall.models.dto.BrandDTO;
import edu.nchu.mall.models.entity.Brand;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "品牌管理")
@Slf4j
@RestController
@RequestMapping("/brands")
public class BrandController {

    @Value("${spring.cloud.alibaba.oss.callbackUrl.product:none}")
    String callbackUrl;

    @Value("${callback.host}")
    String host;

    @Autowired
    BrandService brandService;

    @Autowired
    OSSFeignClient ossFeignClient;

    @Parameters({
        @Parameter(name = "pageNum", description = "页数"),
        @Parameter(name = "pageSize", description = "页面大小")
    })
    @Operation(summary = "获取品牌列表")
    @GetMapping("/list")
    public R<List<Brand>> getBrands(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return R.success(brandService.list(new Page<>(pageNum,pageSize)));
    }

    @Parameters(@Parameter(name = "sid", description = "Brand主键"))
    @Operation(summary = "获取Brand详情")
    @GetMapping("/{sid}")
    public R<Brand> getBrand(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        Brand data = brandService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "brand", description = "要更新的字段")
    })
    @Operation(summary = "更新Brand")
    @PutMapping
    public R<?> updateBrand(@RequestBody Brand brand) {
        if(brand.getBrandId() == null){
            return R.fail("id不能为空");
        }
        boolean res = brandService.updateById(brand);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "brand", description = "新增的Brand"))
    @Operation(summary = "创建Brand")
    @PostMapping
    public ResponseEntity<?> createBrand(@RequestBody BrandDTO dto) throws Exception {
        String callback = callbackUrl + "/brands/callback";
        ObjectMapper mapper = new ObjectMapper();
        //阿里云OSS不支持大写字段
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        Map<String,Object> map = mapper.convertValue(dto, new TypeReference<>() {});
        return ossFeignClient.getPostSignatureForOssUpload(callback, map);
    }

    @PostMapping("/callback")
    public ResponseEntity<R<?>> createBrandCallback(@RequestBody BrandCallback body) {
        log.info("callbackBody : {}", body);
        Brand brand = new Brand();
        BeanUtils.copyProperties(body, brand);
        brand.setLogo(host + body.getFilename());
        boolean res = brandService.save(brand);
        if (!res){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(R.success(null), HttpStatus.OK);
    }

    @Parameters(@Parameter(name = "sid", description = "Brand主键"))
    @Operation(summary = "删除Brand")
    @DeleteMapping("/{sid}")
    public R<?> deleteBrand(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = brandService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页数"),
            @Parameter(name = "pageSize", description = "页面大小")
    })
    @Operation(summary = "导出品牌列表（Excel）")
    @RequestMapping("/export")
    public void exportBrands(@RequestParam Integer pageNum, @RequestParam Integer pageSize, HttpServletResponse response) throws IOException {
        if (pageNum == null || pageSize == null || pageNum < 1 || pageSize < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "pageNum/pageSize must be >= 1");
            return;
        }

        List<Brand> brands = brandService.list(new Page<>(pageNum, pageSize));

        String fileName = "brands-page" + pageNum + "-size" + pageSize + "-" + LocalDate.now() + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("brands");

            int[] widths = {18, 20, 45, 45, 14, 14, 10};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {"品牌ID", "品牌名", "Logo地址", "介绍", "显示状态", "首字母", "排序"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < brands.size(); i++) {
                Brand brand = brands.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(brand.getBrandId() == null ? "" : String.valueOf(brand.getBrandId()));
                row.createCell(1).setCellValue(brand.getName() == null ? "" : brand.getName());
                row.createCell(2).setCellValue(brand.getLogo() == null ? "" : brand.getLogo());
                row.createCell(3).setCellValue(brand.getDescript() == null ? "" : brand.getDescript());
                row.createCell(4).setCellValue(brand.getShowStatus() == null ? "" : String.valueOf(brand.getShowStatus()));
                row.createCell(5).setCellValue(brand.getFirstLetter() == null ? "" : brand.getFirstLetter());
                row.createCell(6).setCellValue(brand.getSort() == null ? "" : String.valueOf(brand.getSort()));
            }

            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }
}
