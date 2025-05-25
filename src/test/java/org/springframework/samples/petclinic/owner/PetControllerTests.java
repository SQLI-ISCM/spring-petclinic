/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(value = PetController.class,
		includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final String PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm"; // New constant for the form path
	private static final String OWNER_ID_PATH = "/owners/{ownerId}/pets/new"; // New constant for the new pet path
	private static final String PET_ID_PATH = "/owners/{ownerId}/pets/{petId}/edit"; // New constant for the edit pet path
	private static final String REQUIRED_FIELD_ERROR_CODE = "required"; // New constant for the required field error code
	private static final String DUPLICATE_FIELD_ERROR_CODE = "duplicate"; // New constant for the duplicate field error code
	private static final String BIRTH_DATE_FIELD = "birthDate"; // New constant for the birth date field
	private static final String NAME_FIELD = "name"; // New constant for the name field
	private static final String HAMSTER = "hamster"; // New constant for the pet type
	private static final String BETTY = "Betty"; // New constant for the pet name
	private static final String BIRTH_DATE = "2015-02-12"; // New constant for the birth date

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@BeforeEach
	void setup() {
		PetType cat = new PetType();
		cat.setId(3);
		cat.setName(HAMSTER);
		given(this.owners.findPetTypes()).willReturn(List.of(cat));

		Owner owner = new Owner();
		Pet pet = new Pet();
		Pet dog = new Pet();
		owner.addPet(pet);
		owner.addPet(dog);
		pet.setId(TEST_PET_ID);
		dog.setId(TEST_PET_ID + 1);
		pet.setName("petty");
		dog.setName("doggy");
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get(OWNER_ID_PATH, TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post(OWNER_ID_PATH, TEST_OWNER_ID).param(NAME_FIELD, BETTY)
				.param("type", HAMSTER)
				.param(BIRTH_DATE_FIELD, BIRTH_DATE))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Nested
	class ProcessCreationFormHasErrors {

		@Test
		void testProcessCreationFormWithBlankName() throws Exception {
			mockMvc
				.perform(post(OWNER_ID_PATH, TEST_OWNER_ID).param(NAME_FIELD, "\t \n")
					.param(BIRTH_DATE_FIELD, BIRTH_DATE))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", NAME_FIELD))
				.andExpect(model().attributeHasFieldErrorCode("pet", NAME_FIELD, REQUIRED_FIELD_ERROR_CODE))
				.andExpect(status().isOk())
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

		@Test
		void testProcessCreationFormWithDuplicateName() throws Exception {
			mockMvc
				.perform(post(OWNER_ID_PATH, TEST_OWNER_ID).param(NAME_FIELD, "petty")
					.param(BIRTH_DATE_FIELD, BIRTH_DATE))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", NAME_FIELD))
				.andExpect(model().attributeHasFieldErrorCode("pet", NAME_FIELD, DUPLICATE_FIELD_ERROR_CODE))
				.andExpect(status().isOk())
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

		@Test
		void testProcessCreationFormWithMissingPetType() throws Exception {
			mockMvc
				.perform(post(OWNER_ID_PATH, TEST_OWNER_ID).param(NAME_FIELD, BETTY)
					.param(BIRTH_DATE_FIELD, BIRTH_DATE))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "type"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "type", REQUIRED_FIELD_ERROR_CODE))
				.andExpect(status().isOk())
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

		@Test
		void testProcessCreationFormWithInvalidBirthDate() throws Exception {
			LocalDate currentDate = LocalDate.now();
			String futureBirthDate = currentDate.plusMonths(1).toString();

			mockMvc
				.perform(post(OWNER_ID_PATH, TEST_OWNER_ID).param(NAME_FIELD, BETTY)
					.param(BIRTH_DATE_FIELD, futureBirthDate))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", BIRTH_DATE_FIELD))
				.andExpect(model().attributeHasFieldErrorCode("pet", BIRTH_DATE_FIELD, "typeMismatch.birthDate"))
				.andExpect(status().isOk())
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

		@Test
		void testInitUpdateForm() throws Exception {
			mockMvc.perform(get(PET_ID_PATH, TEST_OWNER_ID, TEST_PET_ID))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("pet"))
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

	}

	@Test
	void testProcessUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post(PET_ID_PATH, TEST_OWNER_ID, TEST_PET_ID).param(NAME_FIELD, BETTY)
				.param("type", HAMSTER)
				.param(BIRTH_DATE_FIELD, BIRTH_DATE))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Nested
	class ProcessUpdateFormHasErrors {

		@Test
		void testProcessUpdateFormWithInvalidBirthDate() throws Exception {
			mockMvc
				.perform(post(PET_ID_PATH, TEST_OWNER_ID, TEST_PET_ID).param(NAME_FIELD, " ")
					.param(BIRTH_DATE_FIELD, "2015/02/12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", BIRTH_DATE_FIELD))
				.andExpect(model().attributeHasFieldErrorCode("pet", BIRTH_DATE_FIELD, "typeMismatch"))
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

		@Test
		void testProcessUpdateFormWithBlankName() throws Exception {
			mockMvc
				.perform(post(PET_ID_PATH, TEST_OWNER_ID, TEST_PET_ID).param(NAME_FIELD, "  ")
					.param(BIRTH_DATE_FIELD, BIRTH_DATE))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", NAME_FIELD))
				.andExpect(model().attributeHasFieldErrorCode("pet", NAME_FIELD, REQUIRED_FIELD_ERROR_CODE))
				.andExpect(view().name(PETS_CREATE_OR_UPDATE_FORM));
		}

	}

}