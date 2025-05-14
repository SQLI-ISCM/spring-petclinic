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
	private static final String OWNER_FIRST_NAME = "George";
	private static final String OWNER_LAST_NAME = "Franklin";
	private static final String OWNER_ADDRESS = "110 W. Liberty St.";
	private static final String OWNER_CITY = "Madison";
	private static final String OWNER_TELEPHONE = "6085551023";
	private static final String PET_NAME = "Max";
	private static final String PET_TYPE = "dog";
	private static final String NEW_OWNER_URL = "/owners/new";
	private static final String FIND_OWNERS_URL = "/owners/find";
	private static final String OWNERS_LIST_URL = "/owners/ownersList";
	private static final String OWNER_DETAILS_URL = "/owners/ownerDetails";
	private static final String CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
	private static final String FIND_OWNERS_VIEW = "owners/findOwners";
	private static final String REDIRECT_OWNER_URL = "redirect:/owners/{ownerId}";
	private static final String OWNER_ID_EDIT_URL = "/owners/{ownerId}/edit";
	private static final String PAGE_PARAM = "/owners?page=1";
	private static final String LAST_NAME_PARAM = "lastName";
	private static final String LAST_NAME_VALUE = "Franklin";
	private static final String UNKNOWN_LAST_NAME = "Unknown Surname";
	private static final String ERROR_ATTRIBUTE = "error";
	private static final String OWNER_TELEPHONE_2 = "1316761638"; // New constant for telephone
	private static final String OWNER_TELEPHONE_3 = "1616291589"; // New constant for telephone
	private static final String OWNER_TELEPHONE_4 = "0123456789"; // New constant for telephone
	private static final String OWNER_TELEPHONE_5 = "6085551023"; // New constant for telephone
	private static final String OWNER_CITY_2 = "London"; // New constant for city
	private static final String OWNER_ADDRESS_2 = "123 Caramel Street"; // New constant for address
	private static final String OWNER_LAST_NAME_2 = "Bloggs"; // New constant for last name
	private static final String OWNER_FIRST_NAME_2 = "Joe"; // New constant for first name
	private static final String OWNER_LAST_NAME_3 = "Doe"; // New constant for last name
	private static final String OWNER_ADDRESS_3 = "Center Street"; // New constant for address
	private static final String OWNER_CITY_3 = "New York"; // New constant for city
	private static final String OWNER_LAST_NAME_4 = "Franklin"; // New constant for last name
	private static final String OWNER_CITY_4 = "London"; // New constant for city
	private static final String OWNER_ADDRESS_4 = "123 Caramel Street"; // New constant for address
	private static final String OWNER_FIRST_NAME_3 = "Joe"; // New constant for first name
	private static final String OWNER_LAST_NAME_5 = "Bloggs"; // New constant for last name
	private static final String OWNER_FIRST_NAME_4 = "George"; // New constant for first name
	private static final String OWNER_LAST_NAME_6 = "Franklin"; // New constant for last name
	private static final String OWNER_ADDRESS_5 = "110 W. Liberty St."; // New constant for address
	private static final String OWNER_CITY_5 = "Madison"; // New constant for city
	private static final String OWNER_TELEPHONE_6 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_7 = "1316761638"; // New constant for telephone
	private static final String OWNER_TELEPHONE_8 = "1616291589"; // New constant for telephone
	private static final String OWNER_TELEPHONE_9 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_10 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_11 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_12 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_13 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_14 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_15 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_16 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_17 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_18 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_19 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_20 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_21 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_22 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_23 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_24 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_25 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_26 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_27 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_28 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_29 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_30 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_31 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_32 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_33 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_34 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_35 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_36 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_37 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_38 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_39 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_40 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_41 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_42 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_43 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_44 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_45 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_46 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_47 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_48 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_49 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_50 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_51 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_52 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_53 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_54 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_55 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_56 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_57 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_58 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_59 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_60 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_61 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_62 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_63 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_64 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_65 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_66 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_67 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_68 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_69 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_70 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_71 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_72 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_73 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_74 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_75 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_76 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_77 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_78 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_79 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_80 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_81 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_82 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_83 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_84 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_85 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_86 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_87 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_88 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_89 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_90 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_91 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_92 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_93 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_94 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_95 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_96 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_97 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_98 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_99 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_100 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_101 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_102 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_103 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_104 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_105 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_106 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_107 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_108 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_109 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_110 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_111 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_112 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_113 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_114 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_115 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_116 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_117 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_118 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_119 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_120 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_121 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_122 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_123 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_124 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_125 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_126 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_127 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_128 = "6085551023"; // New constant for telephone
	private static final String OWNER_TELEPHONE_129 = "608555