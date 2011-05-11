/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;

import java.util.HashMap;



/**
 * Class Bonxai2XSDConverter.
 *
 * This is the main class of the Bonxai-2-XSD Bonxai2XSDConverter System.
 *
 * It combines the two conversion steps implemented in
 * - GrammarConverter class and
 * - DeclarationConverter class.
 *
 */
public class Bonxai2XSDConverter {

    protected HashMap<String, de.tudortmund.cs.bonxai.xsd.XSDSchema> schemas;

    /**
     * Constructor of this main Bonxai-2-XSD converter class.
     *
     * Initialize the protected "schemas" HashMap as empty HashMap holding all
     * generated XSD Schemas during and after finishing the conversion progress.
     */
    public Bonxai2XSDConverter() {
        this.schemas = new HashMap<String, de.tudortmund.cs.bonxai.xsd.XSDSchema>();
    }

    /**
     * Method convert.
     *
     * This method converts one Bonxai schema to all necessary XSD schemas
     * (one or more) resulting of the different namespaces defined in the
     * original Bonxai Datastructure
     *
     * @param bonxai      -  de.tudortmund.cs.bonxai.bonxai.Bonxai bonxai
     * @return schemas  -  HashMap<String, de.tudortmund.cs.bonxai.xsd.XSDSchema>
     */
    public HashMap<String, de.tudortmund.cs.bonxai.xsd.XSDSchema> convert(de.tudortmund.cs.bonxai.bonxai.Bonxai bonxai) {

        if (bonxai != null) {

            GrammarConverter grammarConverter = new GrammarConverter(this.schemas);
            DeclarationConverter declarationConverter = new DeclarationConverter(schemas);

            grammarConverter.convert(bonxai);
            declarationConverter.convert(bonxai);
        }
        return this.schemas;
    }
}

