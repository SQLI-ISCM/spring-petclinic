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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final String OWNER_FIRST_NAME = "firstName"; // Compliant
	private static final String OWNER_LAST_NAME = "lastName"; // Compliant
	private static final String OWNER_ADDRESS = "address"; // Compliant
	private static final String OWNER_CITY = "city"; // Compliant
	private static final String OWNER_TELEPHONE = "telephone"; // Compliant
	private static final String OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm"; // Compliant
	private static final String OWNER_FIND_OWNERS = "owners/findOwners"; // Compliant
	private static final String OWNER_OWNERS_LIST = "owners/ownersList"; // Compliant
	private static final String OWNER_DETAILS = "owners/ownerDetails"; // Compliant
	private static final String OWNER_UNKNOWN_SURNAME = "Unknown Surname"; // Compliant
	private static final String OWNER_FRANKLIN = "Franklin"; // Compliant
	private static final String OWNER_LONDON = "London"; // Compliant
	private static final String OWNER_MADISON = "Madison"; // Compliant
	private static final String OWNER_BLOGGS = "Bloggs"; // Compliant
	private static final String OWNER_GEORGE = "George"; // Compliant
	private static final String OWNER_MAX = "Max"; // Compliant
	private static final String OWNER_TELEPHONE_NUMBER = "6085551023"; // Compliant
	private static final String OWNER_ADDRESS_VALUE = "110 W. Liberty St."; // Compliant
	private static final String OWNER_NEW = "/owners/new"; // Compliant
	private static final String OWNER_FIND = "/owners/find"; // Compliant
	private static final String OWNER_PAGE = "/owners?page=1"; // Compliant
	private static final String OWNER_EDIT = "/owners/{ownerId}/edit"; // Compliant
	private static final String OWNER_SHOW = "/owners/{ownerId}"; // Compliant

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName(OWNER_GEORGE);
		george.setLastName("Franklin");
		george.setAddress(OWNER_ADDRESS_VALUE);
		george.setCity(OWNER_MADISON);
		george.setTelephone(OWNER_TELEPHONE_NUMBER);
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName(OWNER_MAX);
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		max.setId(1);
		return george;
	}

	@BeforeEach
	void setup() {

		Owner george = george();
		given(this.owners.findByLastNameStartingWith(eq(OWNER_FRANKLIN), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));

		given(this.owners.findAll(any(Pageable.class))).willReturn(new PageImpl<>(List.of(george)));

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet(OWNER_MAX).getVisits().add(visit);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get(OWNER_NEW))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name(OWNER_CREATE_OR_UPDATE_FORM));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post(OWNER_NEW).param(OWNER_FIRST_NAME, "Joe")
				.param(OWNER_LAST_NAME, OWNER_BLOGGS)
				.param(OWNER_ADDRESS, "123 Caramel Street")
				.param(OWNER_CITY, OWNER_LONDON)
				.param(OWNER_TELEPHONE, "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post(OWNER_NEW).param(OWNER_FIRST_NAME, "Joe").param(OWNER_LAST_NAME, OWNER_BLOGGS).param(OWNER_CITY, OWNER_LONDON))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", OWNER_ADDRESS))
			.andExpect(model().attributeHasFieldErrors("owner", OWNER_TELEPHONE))
			.andExpect(view().name(OWNER_CREATE_OR_UPDATE_FORM));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get(OWNER_FIND))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name(OWNER_FIND_OWNERS));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george(), new Owner()));
		when(this.owners.findByLastNameStartingWith(anyString(), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNER_PAGE)).andExpect(status().isOk()).andExpect(view().name(OWNER_OWNERS_LIST));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of(george()));
		when(this.owners.findByLastNameStartingWith(eq(OWNER_FRANKLIN), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNER_PAGE).param(OWNER_LAST_NAME, OWNER_FRANKLIN))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of());
		when(this.owners.findByLastNameStartingWith(eq(OWNER_UNKNOWN_SURNAME), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get(OWNER_PAGE).param(OWNER_LAST_NAME, OWNER_UNKNOWN_SURNAME))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", OWNER_LAST_NAME))
			.andExpect(model().attributeHasFieldErrorCode("owner", OWNER_LAST_NAME, "notFound"))
			.andExpect(view().name(OWNER_FIND_OWNERS));

	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get(OWNER_EDIT, TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty(OWNER_LAST_NAME, is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_FIRST_NAME, is(OWNER_GEORGE))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_ADDRESS, is(OWNER_ADDRESS_VALUE))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_CITY, is(OWNER_MADISON))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_TELEPHONE, is(OWNER_TELEPHONE_NUMBER))))
			.andExpect(view().name(OWNER_CREATE_OR_UPDATE_FORM));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post(OWNER_EDIT, TEST_OWNER_ID).param(OWNER_FIRST_NAME, "Joe")
				.param(OWNER_LAST_NAME, OWNER_BLOGGS)
				.param(OWNER_ADDRESS, "123 Caramel Street")
				.param(OWNER_CITY, OWNER_LONDON)
				.param(OWNER_TELEPHONE, "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post(OWNER_EDIT, TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post(OWNER_EDIT, TEST_OWNER_ID).param(OWNER_FIRST_NAME, "Joe")
				.param(OWNER_LAST_NAME, OWNER_BLOGGS)
				.param(OWNER_ADDRESS, "")
				.param(OWNER_TELEPHONE, ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", OWNER_ADDRESS))
			.andExpect(model().attributeHasFieldErrors("owner", OWNER_TELEPHONE))
			.andExpect(view().name(OWNER_CREATE_OR_UPDATE_FORM));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get(OWNER_SHOW, TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty(OWNER_LAST_NAME, is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_FIRST_NAME, is(OWNER_GEORGE))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_ADDRESS, is(OWNER_ADDRESS_VALUE))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_CITY, is(OWNER_MADISON))))
			.andExpect(model().attribute("owner", hasProperty(OWNER_TELEPHONE, is(OWNER_TELEPHONE_NUMBER))))
			.andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
			.andExpect(model().attribute("owner",
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name(OWNER_DETAILS));
	}

	@Test
	public void testProcessUpdateOwnerFormWithIdMismatch() throws Exception {
		int pathOwnerId = 1;

		Owner owner = new Owner();
		owner.setId(2);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("Center Street");
		owner.setCity("New York");
		owner.setTelephone("0123456789");

		when(owners.findById(pathOwnerId)).thenReturn(Optional.of(owner));

		mockMvc.perform(MockMvcRequestBuilders.post(OWNER_EDIT, pathOwnerId).flashAttr("owner", owner))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + pathOwnerId + "/edit"))
			.andExpect(flash().attributeExists("error"));
	}

}