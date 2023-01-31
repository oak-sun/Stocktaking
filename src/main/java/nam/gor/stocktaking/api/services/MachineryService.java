package nam.gor.stocktaking.api.services;

import lombok.AllArgsConstructor;
import nam.gor.stocktaking.api.dto.machines.MachineryFindDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryOutDTO;
import nam.gor.stocktaking.api.dto.machines.MachinerySaveDTO;
import nam.gor.stocktaking.api.dto.machines.MachineryUpdateDTO;
import nam.gor.stocktaking.infrastucture.dao.intrfc.TaskMasterDao;
import nam.gor.stocktaking.infrastucture.dao.intrfc.MachineryDao;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockDao;
import nam.gor.stocktaking.domain.entities.TaskMaster;
import nam.gor.stocktaking.domain.entities.Machinery;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.MACHINERY_NOT_FOUND;
import static nam.gor.stocktaking.infrastucture.util.StringPatterns.TASKMASTER_NOT_FOUND;

@Service
@AllArgsConstructor
public class MachineryService {

    private final MachineryDao mchDao;
    private final TaskMasterDao tmDao;
    private final StockDao stDao;
    private final IdGenerator idGen;
    private final RequestValidator validator;



    public Flux<MachineryOutDTO> findAllMachines(final MachineryFindDTO dto) {
        return mchDao
                .findByQuery(dto.toEntity())
                .flatMap(this::generatePreSignedUrlAndMapToMachineryDto);
    }

    public Mono<MachineryOutDTO> findMachineryById(final String machineryId) {
        return mchDao
                .findById(machineryId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                MACHINERY_NOT_FOUND,
                                machineryId)))
                .flatMap(this::generatePreSignedUrlAndMapToMachineryDto);
    }

    public Mono<MachineryOutDTO> saveMachinery(final MachinerySaveDTO dto) {
        return validator
                .validate(dto)
                .flatMap(save ->
                        findTaskMasterMachineryById(save.getTaskmasterId())
                        .map(master ->
                                save.toEntity(idGen.newId(),
                                master)))
                .flatMap(machinery -> mchDao
                        .save(machinery)
                        .thenReturn(machinery))
                .flatMap(this::generatePreSignedUrlAndMapToMachineryDto);
    }

    public Mono<Void> updateMachineryById(final String machineryId,
                                 final MachineryUpdateDTO dto) {
        return validator
                .validate(dto)
                .flatMap(update -> mchDao.findById(machineryId))
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                MACHINERY_NOT_FOUND,
                                machineryId)))
                .map(dto::toEntity)
                .flatMap(machinery -> Mono
                        .justOrEmpty(dto.getTaskmasterId())
                        .flatMap(this::findTaskMasterMachineryById)
                        .map(machinery::withTaskMaster)
                        .defaultIfEmpty(machinery))
                .flatMap(mchDao::save)
                .then();
    }

    public Mono<Void> deleteMachineryById(final String machineryId) {
        return mchDao
                .deleteById(machineryId)
                .filter(BooleanUtils::isTrue)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                MACHINERY_NOT_FOUND,
                                machineryId)))
                .then();
    }

    private Mono<TaskMaster> findTaskMasterMachineryById(final String taskmasterId) {
        return tmDao
                .findById(taskmasterId)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(
                                TASKMASTER_NOT_FOUND,
                                taskmasterId)));
    }

    private Mono<MachineryOutDTO> generatePreSignedUrlAndMapToMachineryDto(
                                                       final Machinery machinery) {
        return stDao
                .generatePreSignedUrlForVisualization(
                        machinery.getStockIdentifier())
                .map(stock -> MachineryOutDTO.toDto(machinery, stock));
    }
}

