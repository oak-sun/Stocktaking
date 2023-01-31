package nam.gor.stocktaking.api.services;

import nam.gor.stocktaking.infrastucture.dao.intrfc.StockKeeperDao;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperOutDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperSaveDTO;
import nam.gor.stocktaking.api.dto.stockkeeper.StockKeeperUpdateDTO;
import nam.gor.stocktaking.domain.entities.StockKeeper;
import nam.gor.stocktaking.domain.factories.StockKeeperFactory;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.api.RequestValidator;
import nam.gor.stocktaking.domain.exceptions.EntityNotFoundException;
import nam.gor.stocktaking.domain.exceptions.RequestValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Collections;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockKeeperServiceTest {

    @InjectMocks
    private StockKeeperService service;

    @Mock
    private StockKeeperDao dao;

    @Mock
    private IdGenerator idGen;

    @Mock
    private RequestValidator reqValid;

    @Nested
    @DisplayName("method: findAllStockKeepers()")
    class FindAllStockKeepersMethod {
        private final StockKeeper k1 = StockKeeperFactory.newStockKeeperEntity();
        private final StockKeeper k2 = StockKeeperFactory.newStockKeeperEntity();
        private final StockKeeper k3 = StockKeeperFactory.newStockKeeperEntity();

        @AfterEach
        void tearDown() {
            verify(dao).findAll();
            verifyNoMoreInteractions(dao);
            verifyNoInteractions(reqValid, idGen);
        }

        @Test
        @DisplayName(
                "when called, then it should" + 
                " forward the call to the dao")
        void whenCalled_shouldForwardTheCallToTheDao() {
            when(dao.findAll())
                    .thenReturn(Flux.just(k1, k2, k3));
            StepVerifier
                    .create(service.findAllStockKeepers())
                    .expectSubscription()
                    .expectNext(StockKeeperOutDTO.toDto(k1))
                    .expectNext(StockKeeperOutDTO.toDto(k2))
                    .expectNext(StockKeeperOutDTO.toDto(k3))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("method: saveStockKeeper(StockKeeperSaveDTO)")
    class SaveStockKeeperMethod {
        private final String KEEPER_ID = UUID.randomUUID().toString();
        private final StockKeeperSaveDTO dto = StockKeeperFactory.newSaveStockKeeper();

        private final StockKeeper master = dto.byIdToEntity(KEEPER_ID);

        @AfterEach
        void tearDown() {
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
        }

        @Test
        @DisplayName(
                "when validation fails, then it should" + 
                " not forward more calls")
        void whenValidationFails_shouldNotForwardMoreCalls() {
            final var error = new RequestValidationException(Collections.emptyList());
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(error));
            StepVerifier
                    .create(service.saveStockKeeper(dto))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);
            verifyNoInteractions(idGen, dao);
        }

        @Test
        @DisplayName(
                "when validation succeed, " + 
                "then it should forward the calls")
        void whenValidationSucceed_shouldForwardTheCalls() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(idGen.newId())
                    .thenReturn(KEEPER_ID);
            when(dao.save(master))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.saveStockKeeper(dto))
                    .expectSubscription()
                    .expectNext(StockKeeperOutDTO.toDto(master))
                    .verifyComplete();
            verify(idGen).newId();
            verify(dao).save(master);
            verifyNoMoreInteractions(idGen, dao);
        }
    }

    @Nested
    @DisplayName("method: updateStockKeeperById(String, StockKeeperUpdateDTO)")
    class UpdateStockKeeperByIdMethod {
        private final StockKeeper master = StockKeeperFactory.newStockKeeperEntity();
        private final StockKeeperUpdateDTO dto = StockKeeperFactory.newUpdateStockKeeper();

        private final StockKeeper newMaster = dto.fromEntityToEntity(master);

        @AfterEach
        void tearDown() {
            verify(reqValid).validate(dto);
            verifyNoMoreInteractions(reqValid);
            verifyNoInteractions(idGen);
        }

        @Test
        @DisplayName(
                "when validation fails, " +
                "then it should not forward" + 
                " any more calls")
        void whenValidationFails_shouldNotForwardAnyMoreCalls() {
            final var error = new RequestValidationException(Collections.emptyList());
            when(reqValid.validate(dto))
                    .thenReturn(Mono.error(error));
            StepVerifier
                    .create(service.updateStockKeeperById(master.getId(), dto))
                    .expectSubscription()
                    .verifyError(RequestValidationException.class);
            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName(
                "when stockKeeper is not found," +
                " then it should return an error")
        void whenStockKeeperIsNotFound_shouldReturnAnError() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(dao.findById(master.getId()))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateStockKeeperById(master.getId(), dto))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "StockKeeper with ID %s was not found",
                                    master.getId())
                    );
            verify(dao).findById(master.getId());
            verifyNoMoreInteractions(dao);
        }

        @Test
        @DisplayName(
                "when stockKeeper is found," +
                " then it should update it" +
                " and no errors should happen")
        void whenStockKeeperIsFound_shouldUpdateItAndNoErrorsShouldHappen() {
            when(reqValid.validate(dto))
                    .thenReturn(Mono.just(dto));
            when(dao.findById(master.getId()))
                    .thenReturn(Mono.just(master));
            when(dao.save(newMaster))
                    .thenReturn(Mono.empty());
            StepVerifier
                    .create(service.updateStockKeeperById(master.getId(), dto))
                    .expectSubscription()
                    .verifyComplete();
            verify(dao).findById(master.getId());
            verify(dao).save(newMaster);
            verifyNoMoreInteractions(dao);
        }
    }

    @Nested
    @DisplayName("method: deleteStockKeeperById(String)")
    class DeleteStockKeeperByIdMethod {
        private final String KEEPER_ID = UUID.randomUUID().toString();

        @AfterEach
        void tearDown() {
            verify(dao).deleteById(KEEPER_ID);
            verifyNoMoreInteractions(dao);
            verifyNoInteractions(idGen, reqValid);
        }

        @Test
        @DisplayName(
                "when stockKeeper is deleted successfully," +
                " then it should not return any error")
        void whenStockKeeperIsDeletedSuccessfully_shouldNotReturnAnyError() {
            when(dao.deleteById(KEEPER_ID)).thenReturn(Mono.just(true));
            StepVerifier
                    .create(service.deleteStockKeeperById(KEEPER_ID))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        @DisplayName(
                "when stockKeeper is not " +
                "deleted successfully, " +
                "then it should not return an error")
        void whenStockKeeperIsNotDeletedSuccessfully_shouldNotReturnAnError() {
            when(dao.deleteById(KEEPER_ID))
                    .thenReturn(Mono.just(false));
            StepVerifier
                    .create(service.deleteStockKeeperById(KEEPER_ID))
                    .expectSubscription()
                    .verifyErrorSatisfies(error -> assertThat(error)
                            .isInstanceOf(EntityNotFoundException.class)
                            .hasMessage(
                                    "StockKeeper with ID %s was not found",
                                    KEEPER_ID)
                    );
        }
    }
}

