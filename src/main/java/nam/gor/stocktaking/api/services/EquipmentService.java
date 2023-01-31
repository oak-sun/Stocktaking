package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.api.dto.equipment.EquipmentFindDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentOutDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentSaveDTO;
import nam.gor.stocktaking.api.dto.equipment.EquipmentUpdateDTO;
import nam.gor.stocktaking.domain.entities.Equipment;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.infrastucture.dao.intrfc.EquipmentDao;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockDao;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static nam.gor.stocktaking.infrastucture.util.StringPatterns.EQUIPMENT_NOT_FOUND;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.TASKMASTER_NOT_FOUND;

@Service
@AllArgsConstructor
public class EquipmentService {
    private final EquipmentDao eqDao;
    private final TaskMasterDao tmDao;
    private final StockDao stDao;
    private final IdGenerator idGen;
    private final RequestValidator validator;


    public Flux<EquipmentOutDTO> findAllEquipments(final EquipmentFindDTO dto) {
        return eqDao
                .findByQuery(dto.toEntity())
                .flatMap(this::generatePreSignedUrlAndMapToEquipmentDto);
    }

    public Mono<EquipmentOutDTO> findEquipmentById(final String equipmentId) {
        return eqDao
                .findById(equipmentId)
                .switchIfEmpty(Mono.error(
                           new EntityNotFoundException(
                                   EQUIPMENT_NOT_FOUND,
                                   equipmentId)))
                .flatMap(this::generatePreSignedUrlAndMapToEquipmentDto);
    }

    public Mono<EquipmentOutDTO> saveEquipment(final EquipmentSaveDTO dto) {
        return validator
                .validate(dto)
                .flatMap(save ->
                        findTaskMasterEquipmentById(save.getTaskMasterId())
                                .map(master ->
                                        save.toEntity(idGen.newId(),
                                                master)))
                .flatMap(equipment -> eqDao
                                     .save(equipment)
                                     .thenReturn(equipment))
                .flatMap(this::generatePreSignedUrlAndMapToEquipmentDto);
    }

    public Mono<Void> updateEquipmentById(final String equipmentId,
                                 final EquipmentUpdateDTO dto) {
        return validator
                .validate(dto)
                .flatMap(update -> eqDao.findById(equipmentId))
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                EQUIPMENT_NOT_FOUND,
                                equipmentId)))
                .map(dto::toEntity)
                .flatMap(equipment -> Mono
                                    .justOrEmpty(dto.getTaskMasterId())
                                    .flatMap(this::findTaskMasterEquipmentById)
                                    .map(equipment::withTaskMaster)
                                    .defaultIfEmpty(equipment))
                .flatMap(eqDao::save)
                .then();
    }

    public Mono<Void> deleteEquipmentById(final String equipmentId) {
        return eqDao
                .deleteById(equipmentId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                EQUIPMENT_NOT_FOUND,
                                equipmentId)))
                .then();
    }

    private Mono<TaskMaster> findTaskMasterEquipmentById(final String taskmasterId) {
        return tmDao
                .findById(taskmasterId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                TASKMASTER_NOT_FOUND,
                                taskmasterId)));
    }

    private Mono<EquipmentOutDTO> generatePreSignedUrlAndMapToEquipmentDto(
                                                   final Equipment equipment) {
        return stDao
                .generatePreSignedUrlForVisualization(equipment.getStockIdentifier())
                .map(stock ->
                        EquipmentOutDTO.toDto(equipment, stock));
    }
}
