package com.sparta.team14project.store;

import com.sparta.team14project.menu.dto.MenuResponseDto;
import com.sparta.team14project.menu.entity.Menu;
import com.sparta.team14project.message.MessageResponseDto;
import com.sparta.team14project.order.dto.OrderResponseDto;
import com.sparta.team14project.review.dto.ReviewResponseDto;
import com.sparta.team14project.review.entity.OrderReview;
import com.sparta.team14project.store.dto.StoreMenuResponseDto;
import com.sparta.team14project.store.dto.StoreRequestDto;
import com.sparta.team14project.store.dto.StoreResponseDto;
import com.sparta.team14project.order.entity.Delivery;
import com.sparta.team14project.store.entity.Store;
import com.sparta.team14project.store.repository.StoreRepository;
import com.sparta.team14project.user.entity.User;
import com.sparta.team14project.order.repository.DeliveryRepository;
import com.sparta.team14project.user.login.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final DeliveryRepository deliveryRepository;
    private StoreResponseDto List;

    @CacheEvict(value = "storeCache", allEntries = true)
    public StoreResponseDto createStore(StoreRequestDto requestDto, UserDetailsImpl userDetails) {
        // user 정보 userDetails에서 추출
        User user = userDetails.getUser();

        // requestDto 정보를 저장
        Store store = new Store(requestDto, user);

        // store 정보를 repository에 저장
        Store saveStore = storeRepository.save(store);

        // store 정보를 DTO에 넣어 반환
        StoreResponseDto storeResponseDto = new StoreResponseDto(saveStore);
        return storeResponseDto;
    }

    public List<StoreResponseDto> getStores() {
        return storeRepository.findAllByOrderByStorePointDesc().stream().map(StoreResponseDto::new).toList();
//        return storeRepository.findAll().stream().map(StoreResponseDto::new).toList();
    }

    @Cacheable(value = "storeCache", key = "#keyword")
    public List<StoreResponseDto> getStoreByKeyword(String keyword) {
        return storeRepository.findAllByStoreNameContaining(keyword).stream().map(StoreResponseDto::new).toList();
    }

    @CacheEvict(value = "storeCache", key = "#keyword")
    public void clearStoreCache(String keyword) {
        // 특정 키워드의 캐시를 지움
    }

    @CacheEvict(value = "storeCache", allEntries = true)
    public void clearAllStoreCaches() {
    }

    @Transactional
    @CacheEvict(value = "storeCache", allEntries = true)
    public StoreResponseDto updateStore(Long id, StoreRequestDto requestDto) {
        Store store = findStore(id);
        store.update(requestDto);
        return new StoreResponseDto(store);
    }

    @CacheEvict(value = "storeCache", allEntries = true)
    public MessageResponseDto deleteStore(Long id) {
        Store store = findStore(id);
        storeRepository.delete(store);
        MessageResponseDto messageResponseDto = new MessageResponseDto(
                "업장 삭제가 완료되었습니다.", 200
        );
        return messageResponseDto;
    }

    @Transactional
    public MessageResponseDto deliveryDone(Long orderId, User user) {
        Delivery delivery = deliveryRepository.findById(orderId).orElseThrow(()->
                new IllegalArgumentException("유효하지 않은 주문ID 입니다.")
        );
        Store store = delivery.getStore();
        if(store.getUser().getId() != user.getId())
            throw new IllegalArgumentException("주문을 받은 가게의 주인만 배달 완료를 진행할 수 있습니다.");
        delivery.deliveryDone();
        return new MessageResponseDto("배달이 완료되었습니다!", 200);
    }

    public List<OrderResponseDto> deliveryCheck(Long storeId) {
        Store store = findStore(storeId);
        List<OrderResponseDto> responseDtos = deliveryRepository.findAllByStore(store).stream().map(OrderResponseDto::new).collect(Collectors.toList());
        return responseDtos;
    }

    private Store findStore(Long id) {
        return storeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("데이터가 없습니다.")
        );
    }

    public Long getStoreId(UserDetailsImpl userDetails) {
        return storeRepository.findStoreByUser(userDetails.getUser()).getId();
    }

    public StoreMenuResponseDto getStore(Long id){
        Store store = findStore(id);

        StoreMenuResponseDto storeInfo = new StoreMenuResponseDto(store);
        return storeInfo;
    }


}
