package com.wentgo.retrofit.flow.lib

import java.lang.StringBuilder
import java.lang.reflect.*
import java.util.*

/**
 * @author wentgo
 * @version 1.0
 * @time 2022/03/21
 * @description
 */
class ParameterizedTypeImpl(ownerType: Type?, rawType: Type, vararg typeArguments: Type) :
    ParameterizedType {
    private val ownerType: Type?
    private val rawType: Type
    private val typeArguments: Array<Type>
    override fun getActualTypeArguments(): Array<Type> {
        return typeArguments.clone()
    }

    override fun getRawType(): Type {
        return rawType
    }

    override fun getOwnerType(): Type? {
        return ownerType
    }

    override fun equals(other: Any?): Boolean {
        return other is ParameterizedType && equalsType(this, other)
    }

    override fun hashCode(): Int {
        return (typeArguments.contentHashCode()
                xor rawType.hashCode()
                xor (ownerType?.hashCode() ?: 0))
    }

    override fun toString(): String {
        if (typeArguments.isEmpty()) return typeToString(rawType)
        val result = StringBuilder(30 * (typeArguments.size + 1))
        result.append(typeToString(rawType))
        result.append("<").append(
            typeToString(
                typeArguments[0]
            )
        )
        for (i in 1 until typeArguments.size) {
            result.append(", ").append(
                typeToString(
                    typeArguments[i]
                )
            )
        }
        return result.append(">").toString()
    }

    companion object {
        fun equalsType(a: Type, b: Type): Boolean {
            return when {
                a === b -> {
                    true // Also handles (a == null && b == null).
                }
                a is Class<*> -> {
                    a == b // Class already specifies equals().
                }
                a is ParameterizedType -> {
                    if (b !is ParameterizedType) return false
                    val ownerA: Any? = a.ownerType
                    val ownerB: Any = b.ownerType
                    ((ownerA === ownerB || ownerA != null && ownerA === ownerB)
                            && a.rawType == b.rawType && Arrays.equals(
                        a.actualTypeArguments,
                        b.actualTypeArguments
                    ))
                }
                a is GenericArrayType -> {
                    if (b !is GenericArrayType) return false
                    equalsType(
                        a.genericComponentType,
                        b.genericComponentType
                    )
                }
                a is WildcardType -> {
                    if (b !is WildcardType) return false
                    (Arrays.equals(a.upperBounds, b.upperBounds)
                            && Arrays.equals(a.lowerBounds, b.lowerBounds))
                }
                a is TypeVariable<*> -> {
                    if (b !is TypeVariable<*>) return false
                    val va = a
                    val vb = b
                    (va.genericDeclaration === vb.genericDeclaration
                            && va.name == vb.name)
                }
                else -> {
                    false // This isn't a type we support!
                }
            }
        }

        fun typeToString(type: Type): String {
            return if (type is Class<*>) type.name else type.toString()
        }
    }

    init {
        // Require an owner type if the raw type needs it.
        require(
            !(rawType is Class<*>
                    && ownerType == null != (rawType.enclosingClass == null))
        )
        this.ownerType = ownerType
        this.rawType = rawType
        this.typeArguments = typeArguments.clone() as Array<Type>
    }
}