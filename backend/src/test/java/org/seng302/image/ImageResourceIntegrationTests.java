package org.seng302.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.seng302.Main;
import org.seng302.controller.ImageResource;
import org.seng302.model.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.seng302.model.enums.BusinessType;
import org.seng302.model.enums.Role;
import org.seng302.model.repository.BusinessRepository;
import org.seng302.model.repository.ImageRepository;
import org.seng302.model.repository.ProductRepository;
import org.seng302.model.repository.UserRepository;
import org.seng302.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ImageResource test class
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Main.class})
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ImageResourceIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ImageRepository imageRepository;

    private FileStorageService fileStorageService;

    private User user;

    private User dGAA;

    private User gAA;

    private User anotherUser;

    private Business business;

    private Business anotherBusiness;

    private Product product;

    private MockMultipartFile jpgImage;

    private MockMultipartFile jpegImage;

    private MockMultipartFile pngImage;

    private MockMultipartFile gifImage;

    private MockMultipartFile otherFile;

    private Image primaryImage;

    private String productId;

    private Integer businessId;

    private String sessionToken;

    private MockHttpServletResponse response;


    @BeforeEach
    public void setup() throws Exception{
        Address address = new Address(
                "3/24",
                "Ilam Road",
                "Christchurch",
                "Canterbury",
                "New Zealand",
                "90210",
                "Ilam"
        );

        dGAA = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2000, 2, 2),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.DEFAULTGLOBALAPPLICATIONADMIN);
        dGAA.setId(1);
        dGAA.setSessionUUID(User.generateSessionUUID());
        gAA = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2000, 2, 2),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.GLOBALAPPLICATIONADMIN);
        gAA.setId(2);
        gAA.setSessionUUID(User.generateSessionUUID());
        user = new User("first",
                "last",
                "middle",
                "nick",
                "bio",
                "example@example.com",
                LocalDate.of(2000, 1, 1),
                "123456789",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(3);
        user.setSessionUUID(User.generateSessionUUID());
        anotherUser = new User("first",
                "last",
                "middle",
                "nick",
                "bio",
                "example@example.com",
                LocalDate.of(2000, 1, 1),
                "123456789",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(4);
        anotherUser.setSessionUUID(User.generateSessionUUID());

        business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0)),
                user
        );
        business.setId(1);

        anotherBusiness = new Business(
                user.getId(),
                "anotherName",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0)),
                user
        );
        anotherBusiness.setId(2);
        user.setBusinessesAdministeredObjects(List.of(business, anotherBusiness));

        product = new Product(
                "PROD",
                business,
                "Beans",
                "Description",
                "Manufacturer",
                20.00,
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0))
        );

        otherFile = new MockMultipartFile("images", "testImage.other", "something", this.getClass().getResourceAsStream("testImage.jpg"));
        jpgImage = new MockMultipartFile("images", "testImage.jpg", MediaType.IMAGE_JPEG_VALUE, this.getClass().getResourceAsStream("testImage.jpg"));
        jpegImage = new MockMultipartFile("images", "testImage.jpeg", MediaType.IMAGE_JPEG_VALUE, this.getClass().getResourceAsStream("testImage.jpg"));
        pngImage = new MockMultipartFile("images", "testImage.png", MediaType.IMAGE_PNG_VALUE, this.getClass().getResourceAsStream("testImage.jpg"));
        gifImage = new MockMultipartFile("images", "testImage.gif", MediaType.IMAGE_GIF_VALUE, this.getClass().getResourceAsStream("testImage.jpg"));

        productId = product.getProductId();
        businessId = business.getId();

        primaryImage = new Image(1, productId, businessId, "storage/test", "test/test", true);
        fileStorageService = Mockito.mock(FileStorageService.class, withSettings().stubOnly());

        this.mvc = MockMvcBuilders.standaloneSetup(new ImageResource(businessRepository, userRepository, productRepository, imageRepository, fileStorageService)).build();
    }

    //------------------------------------ Product Image Creation Endpoint Tests ---------------------------------------

    /**
     * Testing that we receieve a image id and get a CREATED http status back when we create a image for a products
     * that does not have already any other primary images.
     * @throws Exception
     */
    @Test
    void testingFileCreationWithValidDataNoPrimaryImages() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpgImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }

    /**
     * Testing that we receieve a image id and get a CREATED http status back when we create a image for a products
     * that has already a primary image.
     * @throws Exception
     */
    @Test
    void testingFileCreationWithValidDataWithPrimaryImages() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        images.add(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpgImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }

    /**
     * Testing that we get a BAD_REQUEST https status when we do not include the 'images' file.
     * @throws Exception
     */
    @Test
    void testingImageFileIsRequired() throws Exception {
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Test that the user must provide a cookie.
     * @throws Exception
     */
    @Test
    void TestingUserHasToHaveCookie() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();

        // When
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    /**
     * Testing that a cookie must have a valid active session token.
     */
    @Test
    void TestingThatUserMustHaveActiveValidSession() throws Exception {
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.empty());
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

    }

    /**
     * Testing that the user must provide a valid business id in the parameters.
     * @throws Exception
     */
    @Test
    void testingUserHasToProvideValidBusinessId() throws Exception {
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(gAA));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.empty());
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }


    /**
     * Testing that the user must provide a valid product id in the parameters.
     * @throws Exception
     */
    @Test
    void testingUserHasToProvideValidProductId() throws Exception {
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(gAA));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.empty());
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    /**
     * Testing that a user needs to have GAA role to be able to upload an image for a product they are not on the administrator list of.
     * @throws Exception
     */
    @Test
    void testingUserNeedsToBeGaaForBusinessThatTheyAreNotAdminOf() throws Exception{
        // Given
        businessId = anotherBusiness.getId();
        productId = product.getProductId();

        sessionToken = gAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(gAA));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(anotherBusiness));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        images.add(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }


    /**
     * Testing that a user needs to have DGAA role to be able to upload an image for a product they are not on the administrator list of.
     * @throws Exception
     */
    @Test
    void testingUserNeedsToBeDgaaForBusinessThatTheyAreNotAdminOf() throws Exception{
        // Given
        businessId = anotherBusiness.getId();
        productId = product.getProductId();

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(dGAA));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(anotherBusiness));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        images.add(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }

    /**
     * Testing that a user of role USER can upload images if they are an admin of the business.
     * @throws Exception
     */
    @Test
    void testingThatUserMustBeAdminOfBusinessToUploadImages() throws Exception{
        // Given
        businessId = anotherBusiness.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(anotherBusiness));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        images.add(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }

    /**
     * Testing that a user of role USER cannot upload images if they are not an admin of the business.
     * @throws Exception
     */
    @Test
    void testingThatUserThatIsNotAnAdminOfBusinessCannotUploadImages() throws Exception{
        // Given
        businessId = anotherBusiness.getId();
        productId = product.getProductId();

        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(anotherUser));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(anotherBusiness));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        images.add(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    /**
     * Testing that a user cannot upload a file that is not of type .gif, .gif, .jpg or .jpeg.
     * @throws Exception
     */
    @Test
    void testingInvalidFileFormat() throws Exception {
        // Given
        businessId = anotherBusiness.getId();
        productId = product.getProductId();

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(dGAA));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(anotherBusiness));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(otherFile).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }


    /**
     * Testing that .gif is a valid file format to be uploaded.
     * @throws Exception
     */
    @Test
    void testingThatGifIsAnAcceptableFormat() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(gifImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }


    /**
     * Testing that .png is a valid file format to be uploaded.
     * @throws Exception
     */
    @Test
    void testingThatPngIsAnAcceptableFormat() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(pngImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }


    /**
     * Testing that .gif is a valid file format to be uploaded.
     * @throws Exception
     */
    @Test
    void testingThatJpegIsAnAcceptableFormat() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpegImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }


    /**
     * Testing that .gif is a valid file format to be uploaded.
     * @throws Exception
     */
    @Test
    void testingThatJpgIsAnAcceptableFormat() throws Exception{
        // Given
        businessId = business.getId();
        productId = product.getProductId();

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.generateThumbnail(any(MultipartFile.class), anyString())).thenReturn(
                new ByteArrayInputStream("mockedThumbnailInputStream".getBytes())
        );
        lenient().when(fileStorageService.storeFile(any(InputStream.class), anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = new ArrayList<>();
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.saveAndFlush(any(Image.class))).thenReturn(primaryImage);
        response = mvc.perform(multipart(String.format("/businesses/%d/products/%s/images", businessId, productId)).file(jpgImage).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(String.format("{\"id\":%d}", primaryImage.getId()));
    }



    //------------------------------------ Product Image Deletion Endpoint Tests ---------------------------------------

    /**
     * Tests that an OK status is received when deleting an image of an existing business with an existing product at
     * the file path product-images -> IMAGE_UUID and that the image no longer exists at the file path
     * location.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdExistsAtExpectedFilePathLocation_thenDeleteImageWithGivenImageIdAtFilePathLocation() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.deleteFile(anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = List.of(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.findImageByIdAndBusinessIdAndProductId(primaryImage.getId(), businessId, productId)).thenReturn(Optional.of(primaryImage));
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }

    /**
     * Tests that another existing image is made the primary image when the current primary image is deleted and an OK response is
     * returned.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndMultipleImagesExist_thenDeleteImageWithGivenImageIdAtFilePathLocation() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.deleteFile(anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        Image newImage = new Image(2, productId, businessId, "storage/test2", "test2/test2", false);
        List<Image> images = List.of(newImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(Collections.emptyList());
        when(imageRepository.findImageByIdAndBusinessIdAndProductId(primaryImage.getId(), businessId, productId)).thenReturn(Optional.of(primaryImage));
        when(imageRepository.findImageByBusinessIdAndProductId(businessId, productId)).thenReturn(images);
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(images.get(0).getIsPrimary()).isTrue();

    }

    /**
     * Tests that a FORBIDDEN status is received when deleting an image of an existing business with an existing product
     * at the file path product-images -> IMAGE_UUID but the user does not have administration rights i.e.
     * not administrator of business.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdExistsAtExpectedFilePathLocationButIncorrectAccessRights_thenReceiveForbiddenStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(anotherUser));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when deleting an image of an existing business with an existing
     * product but the image id does not exist
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdDoesNotExistsAtExpectedFilePathLocation_thenReceiveNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.deleteFile(anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = List.of(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.findImageByIdAndBusinessIdAndProductId(primaryImage.getId(), businessId, productId)).thenReturn(Optional.empty());
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());

    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when deleting an image of an existing business with a non-existing
     * product.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdDoesNotExist_thenReceiveNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.empty());
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", businessId, "A9000", primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when deleting an image of an non-existing business.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsDoesNotExist_thenReceiveNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.empty());
        response = mvc.perform(delete(String.format("/businesses/%d/products/%s/images/%d", 8000, "A9000", primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    //--------------------------- Product Image Changing Primary Image Endpoint Tests ----------------------------------

    /**
     * Tests that an OK status is received when making an image the primary image of an existing business with an existing product at
     * the file path product-images -> IMAGE_UUID and that the image no longer exists at the file path
     * location.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdExistsAtExpectedFilePathLocation_thenMakeNewPrimaryImageWithGivenImageIdAtFilePathLocation() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        List<Image> images = List.of(primaryImage);
        Image newImage = new Image(2, productId, businessId, "storage/test2", "test2/test2", false);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.findImageByIdAndBusinessIdAndProductId(primaryImage.getId(), businessId, productId)).thenReturn(Optional.of(newImage));
        response = mvc.perform(put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(newImage.getIsPrimary()).isTrue();

    }

    /**
     * Tests that a FORBIDDEN status is received when making an image the primary image of an existing business with an existing product
     * at the file path product-images -> IMAGE_UUID but the user does not have administration rights i.e.
     * not administrator of business.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdExistsAtExpectedFilePathLocationButIncorrectAccessRights_thenChangingPrimaryImageResultsInForbiddenStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(anotherUser));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        response = mvc.perform(put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when  making an image the primary image of an existing business with an existing
     * product but the image id does not exist
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdExistsAndImageWithGivenImageIdDoesNotExistsAtExpectedFilePathLocation_thenChangingPrimaryImageResultsInNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.of(product));
        lenient().when(fileStorageService.deleteFile(anyString())).thenReturn(true);
        lenient().when(fileStorageService.getPathString(anyString())).thenReturn(primaryImage.getFilename());
        List<Image> images = List.of(primaryImage);
        when(imageRepository.findImageByBusinessIdAndProductIdAndIsPrimary(businessId, productId, true)).thenReturn(images);
        when(imageRepository.findImageByIdAndBusinessIdAndProductId(primaryImage.getId(), businessId, productId)).thenReturn(Optional.empty());
        response = mvc.perform(put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", businessId, productId, primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());

    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when  making an image the primary image of an existing business with a non-existing
     * product.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsAndProductIdDoesNotExist_thenChangingPrimaryImageResultsInNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.of(business));
        when(productRepository.findProductByIdAndBusinessId(productId, businessId)).thenReturn(Optional.empty());
        response = mvc.perform(put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", businessId, "A9000", primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    /**
     * Tests that a NOT_ACCEPTABLE status is received when  making an image the primary image of an non-existing business.
     *
     * @throws Exception Exception error
     */
    @Test
    void whenBusinessIdExistsDoesNotExist_thenChangingPrimaryImageResultsInNotAcceptedStatus() throws Exception {

        // Given
        businessId = business.getId();
        productId = product.getProductId();
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // When
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.of(user));
        when(businessRepository.findBusinessById(businessId)).thenReturn(Optional.empty());
        response = mvc.perform(put(String.format("/businesses/%d/products/%s/images/%d/makeprimary", 8000, "A9000", primaryImage.getId())).cookie(cookie)).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

}