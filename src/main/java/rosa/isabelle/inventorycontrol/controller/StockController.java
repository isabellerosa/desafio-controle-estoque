package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.dto.StockDTO;
import rosa.isabelle.inventorycontrol.dto.StockItemDTO;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.model.request.StockRequestModel;
import rosa.isabelle.inventorycontrol.model.response.StockItemModel;
import rosa.isabelle.inventorycontrol.model.response.StockResponseModel;
import rosa.isabelle.inventorycontrol.service.StockService;

@RestController
@RequestMapping("{ownerId}/store/{storeId}/stock")
public class StockController {

    private StockService stockService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockController.class);


    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StockItemModel createStock(@RequestBody StockRequestModel newStockRequest,
                               @PathVariable("storeId") String storeId) {
        LOGGER.debug("Starting createStock with storeId: {} and itemId: {}", storeId, newStockRequest.getItem());

        try {
            ModelMapper modelMapper = new ModelMapper();

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setPublicId(storeId);

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setPublicId(newStockRequest.getItem());

            StockItemDTO newStockItemDTO = modelMapper.map(newStockRequest, StockItemDTO.class);
            newStockItemDTO.setItem(itemDTO);
            newStockItemDTO.setStore(storeDTO);

            StockItemDTO createdStockItemDTO = stockService.addStockItem(newStockItemDTO);

            StockItemModel createdStockItemResponse = modelMapper.map(createdStockItemDTO, StockItemModel.class);

            return createdStockItemResponse;
        }catch (CustomException customException){
            LOGGER.error("An exception occurred at createStock with storeId: {} and itemId: {}",
                    storeId, newStockRequest.getItem());

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            LOGGER.error("An exception occurred at createStock with storeId: {} and itemId: {}",
                    storeId, newStockRequest.getItem());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    StockResponseModel findStocks(@PathVariable("storeId") String storeId,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "15") int size) {
        LOGGER.debug("Starting findStocks with storeId: {}", storeId);

        try {
            ModelMapper mapper = new ModelMapper();

            StockDTO stockPageDTO = stockService.getStock(storeId, page - 1, size);

            StockResponseModel stockPageResponse = mapper.map(stockPageDTO, StockResponseModel.class);

            return stockPageResponse;
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred at findStocks with storeId: {}", storeId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        } catch (Exception exception) {
            LOGGER.error("An exception occurred at findStocks with storeId: {}", storeId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StockItemModel updateStock(@RequestBody StockItemModel editedStockItemRequest,
                               @PathVariable("itemId") String itemId,
                               @PathVariable("storeId") String storeId) {
        LOGGER.debug("Starting updateStock with itemId: {} and storeId: {}", itemId, storeId);

        try {
            ModelMapper mapper = new ModelMapper();
            StockItemDTO editedStockItemDTO = mapper.map(editedStockItemRequest, StockItemDTO.class);

            StockItemDTO modifiedStockItemDTO = stockService.editStockItem(storeId, itemId, editedStockItemDTO);

            StockItemModel modifiedStockItemResponse = mapper.map(modifiedStockItemDTO, StockItemModel.class);

            return modifiedStockItemResponse;
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred at updateStock with itemId: {} and storeId: {}", itemId, storeId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        } catch (Exception exception) {
            LOGGER.error("An exception occurred at updateStock with itemId: {} and storeId: {}", itemId, storeId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    StockItemModel deleteStock(@PathVariable("itemId") String itemId, @PathVariable("storeId") String storeId) {
        LOGGER.debug("Starting deleteStock with itemId: {} and storeId: {}", itemId, storeId);

        try {
            ModelMapper modelMapper = new ModelMapper();

            StockItemDTO deletedStockItemDTO = stockService.removeStockItem(storeId, itemId);

            StockItemModel deletedStockItemResponse = modelMapper.map(deletedStockItemDTO, StockItemModel.class);

            return deletedStockItemResponse;
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred at deleteStock with itemId: {} and storeId: {}", itemId, storeId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        } catch (Exception exception) {
            LOGGER.error("An exception occurred at deleteStock with itemId: {} and storeId: {}", itemId, storeId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
