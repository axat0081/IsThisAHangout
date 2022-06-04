package com.example.isthisahangout.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class ProfileData(
    val userId: String,
    val username: String,
    val pfp: String,
    val header: String
)

const val DEFAULT_PFP: String =
    "https://preview.redd.it/bcyq3rjk2w071.png?auto=webp&s=97c9b873f1b41a7b9ff31331fd92f2e3fafed92f"
const val DEFAULT_HEADER: String =
    "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMTEhUSEhMVFRUXFRUYFxcYFRUXFxcXFxUWFhUXFxUYHSggHiAlHRcdIT0tJSkrLi4uFx8zODMtNygtLysBCgoKBwcGGgcHGisZExkrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrKysrK//AABEIAIUBewMBIgACEQEDEQH/xAAZAAADAQEBAAAAAAAAAAAAAAAAAQIDBAX/xAAnEAACAgICAwEAAgMBAQEAAAABAgARAzESISJCYUEEURMycaGBkf/EABQBAQAAAAAAAAAAAAAAAAAAAAD/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwDwlUsbOo8mT1WGTJ6rGAEH2AABB9maoWsxA2fKW6lTY1AnEwBNylUsbOpTKGFjcy/yGuMDTJk9VjACD7AAIPsnGl+TQDGl+TRZMl9DUM2W+hqNcQI63AjJjqW+S+ljx5L8WklShuBYAQfZONL8m1DGvLswdyxoagDuWNDUpiFFDcGIQUNzLFRPcBjGSLhhyAA/3K7Q/I8mO+xAWNOXk2oO5Y0NSeZNLNGIQUNwBiEFDcWNPZoY09mmeXJf/IFM3I1oSSlHvUs4gRaykcMKO4Eu5boalEhBQ3M7KGXiT2aAY8fs0RJc0NQJLn5KyOFFCAZHCihMjiNXHhUG73KVipo6gLHlAH2VjT2aLLi/RJ5FjUCiS5oalO/Hobg78ehuCJx7O4AicezuZ9uZLvZ71NHx15LAhRR8pZJc0NSgQ4+zMMVsQNHfj0NwROPbbixqAORiALmzqAAFz3qPLlroQy5K6EnFjBH2BL4iBcsZaFDcMb14tFkx8TY1AtE4+TbkgFz8iW3PcrI/qsAyP6rGP4/9mCgILO5ichP6YGmNwBf7IKk+UWRRfUvG5Xo6gWKcf0RJxvXi0eTHXksfTj7Al1KmxqUyhhY3JxvXi0HUqbGoEJvy/wDZeR+RoahlIIv9mag7H5AvE3EkGN0KmxqUKcfZON68WgUyhhY3FjyerROhU2NSmUMLG4GWVCP+TQZAo63DHk9WmJXuh3Aoqf8AYzVlDCxuTjyV4tB1KmxqA8eS/Fou0PyUyhhY3Fje/FoBkS/JZGEi7aV2h+RZ62IAzFj1qPC3qZmtjubMA4sbgSQUPyVkS/JYsb+rREFD8gUjhhR3MXUjo6m2RL8l3BXDCmgJ8gApZABUgkSAtmh3NseS/FoDyJfksEcMKO5JBQ/JWRL8lgSrFTR1DLj9llI4YUdyVYqaOoCwkCydxG27/qGcD8/YkJXv8MDTEwI4mJSUNHUrInLyXcEfkKO4CyY/ZYwQ4+yVJQ0dR5MfssDIr3RmuTJ6rDkGHfRmKqTqBaeJ7EvInssaOGFHclSVNHUCgQ4+ycb14tHkx+yxghx9gRlx12NR4mAF/sMb14tIyqAeoAQW7mq/yBWpCMV3qa/4lPcCUQKLO5AUub0I1BY2dR5MnqsCceTia/JWTHXksTYwB3uTiyVvUDTpx9kpk49NDKteQiRSxs6gLFjv/kvI/qsMj+qxgBB9gZunGjfc0FOPslE5eTakMe7WBeN68Wg6FTY1KBDj7Mgx/wBYDyNyIodzQAIPsAAg+ycaX5NAQxluz1DDl/DqN3LGhqLKoAr9gN0KmxqUyhhY3JxZK6Oosg4mxAP8vRBEMOK+zqPHjvyMHcsaGoA78jQ1JYcT0ZoxCihuJMfs0BsA4sbixv6tMg1GxNmAYX+wJIKH5JbyPQiDFupqSEFDcAJCChuZ/wCI0SZeNPZoiS5+QHiyX0YiCh+RZgBQG5WPJfi0B5EvyWQ2WxRHcTWp6l4sfs0AxYq7MRJc/IElzQ1Kd+PQ3Azvif7mjpy8l3EuMAW0yxuR3+QNkfkKO5IJQ0dR5VBHISBbf/ICC8j10Jq78ehuDvx6G4InHs7gZtioXfc0Rwwo7kgFz8k5avxgUpKmjqPJj9ljRwwo7mfIrYgGTLYHXc0xoFFmGNAosyQC571AQBc3oTNuuptkyeqxj+P/AGYCyP6rGAEH2AAQfZONL8mgGNL8mk5sl60JWR+RoaixNxJBgThx3/yaZH9Vk5cddjUeJgBf7AoAIPsnGnLyaGNL8m1B3LGhqAO5Y0NSmIQUNwYhBQ3Mip/2gQJ0ABB9gyhhY3Mk35f+wLxpfk0HcsaGoO5Y0NSmIUUNwBiEFDcWPH7NDHj9mksxY9QJyvZlYcd9nQjwsP8AUyMqEf8AIFu5Y0NSmIQUNxDIFHW4Y8fs0Ax4/ZoiS5+QJLn5KyOFFCAsrADiJios1LAKkEiaZEvyWAMQgobixp7NIwkXbSiS5+QAkufkrI/HxXcHfj4ruCJxFncAROIs7mDtZuWbbuXiII4mAsKexgSXPyZupHR1NWyAClgN34+K7gicezuCIFFnckAubOoAAXNnUMzj/UR5cnqshbU9iBONbNTZ349DcWXH7LJwkCydwLROPZ3JALn5AAubOo8mT1WAZH9VjACCzuAAQWdzIgnygQxs3N8agDkY1phX6JiV7owNAC5s6jyZPVYZMnqsYAQWdwAAILO5nbHvuUicjZ1KP8gf1AnGnLybUWbLfQ1IYkeJmmEAijuANirtTKBDj7JVipo6jyJXksBY3rxaLLirsalghx9k43rxaAmct0OpbEKKG5ORCvYmaN3Z7gVjTldnuVjevFo8mP2WMEOPsCXQqbGpTKGFjcnG9eLQdCpsagSmTiCK7l409mjZQwsbmLMdH8gVkycj/QlMhXsajRQwrRixvXi0CmUMLG4sb34tE6FTY1KZQwsbgZsnE3sRklz/AEJWPJ6tIdSutGBpkcKKEzx4rG+4sRF9y3QqbGoDxv6tEQUPyUwDixuLG/q0B5EDCxM1y0KA7lEFD8lZE5drAEQKLO5ny5HvqQ7E7m3AMOujARBQ/JWRL8lixv6tEQUPyBSOGFHczA4nvuaZEvyWCOGFHcCVBc2dQy5fwSGtevyPARo/sBjDYsHuWj8hR3JIKH5KdOXku4EqSho6hlxfolI/IUdyQSho6gL/ACkgACWAEFncWTH7LMS1mz3AtByPZlKSho6jfGCLWNHDCjuAsmP2WMEOPslSUNHUeTH7LAhG4k2JSJy7OpQIcfZixI8TAvNlvoalrgBGzIw0ejuI4WgNFLGzqTkq/H/yaZHvxWMUg+wBHDCjuSrFTR1I4Ejl/wDZqjhhR3AWTH7LGCHH2SrFTR1IcAnxgAY/6y3QAd7lABB9k40vyaBOLIRvUvJj9liduRoRI5U0dQLBDj7JxvXi0eRPZYsmQEfYCyDibEePHfk0WHFfZ1G7ljQ1Ahj3azUEOPsGYKKG5lwIHKBeN68Wg6FTY1KFOPslHrxaBTgMLG5kGLdRcbNLNmIQUNwJyqAK/YYsldHUePH7NJa3PUBuhU2NSmAYWNyceSvE6g6FTY1AeN/VpDWp6MeZwR9lYsdeTQDHi/WmQajYmhJc/I8jADiIDYBhY3FjyerSOJWjNGAcWNwJIKH5HlUEchBMv40yRLPWoFC2/wDkeYAUBuXkfj4ruCJxFncBYsnq0RBQ9ak8SxuXiyX0YDdOXa7gj8hR3JIKGxqTmYGq3AXIrYE0XGALaNECizuSAXNnUDPG5Hf5NnTl5LuTlf1WT2hgaI/IUdyVJQ0dSsicvJdyRlBFNAMq15CJVLGzqTix3/yaZH9VgRlq/GUP5B/qUAEFncxKk91uBsKQfZONL8mhjS/JoOxY0NQB3LGhqTlQCqPc0ZgoobmeNOV99wBshahU0ACD7M0biTYlY0vyaAY0vyaDuWNDUHcsaGpTMFFDcAZgoobkri6to8eP2aRkycj/AEICx5SJeLFfZ1Jy4q7HYjZy3Q6gN3LGhqUzBRQ3BmCihuLGns0Ax4/ZoiS5+QJLn5HkycehAjIAp6gzFiOoJisXfcEycbFdwNGIUUNxY09mhjT2aIkufkAJLn5KyPXiu4ZHrxWCIFFncCTioWdyFykCocuR76jdOJvYgXix12YiS5+RElz/AEJeR68VgGR68VgiBRZ3BECizuSoLGzqAKC5s6kE0fGXly/giGGxYgLtz/U0d+Piu5muWhQE0RAos7gCJxFnclQXNnUFBc2dR5H9VgGR/VZOTEAPssAIPszTyPZgL/Kaqa40CizuZ1xP9ylBc2dQAAubOo8mT1WGTJ6rGAEH2AABB9konLs6gicuzqLNlvoagSr0etSlTkb/ACD4erHcP8poACBWR/VYwAgs7gAEFnclE5dnUAROXZ1KP8gf1Jd+XQ1NBhEDN2LGhqUzBRQ3ITKAOtxDGSLgJejbCaZMdeSxo4YUdyVYqaOoFAhx9mLWPEzXInssYIcfYAzBRQ3Fjx15NM08T2JRbka1AWRi3/BLxqGFaMSkoaOo8mP2WAsb14tDIhXsalAhx9k43rxaAYQO2JgSXPyLNirsagc1ChAvI9eKzPHQPkP/ANh/jNBh/wBmgIcfYEuhU2NSmUMLG5ON68Wg6FTY1AiyaBM1d68VgyhhY3M8TAE2IGiIFFncyck9/kYtzKxvx8TqA+AYddGGPJ6tE6cTY1KIDixuBDqV1qPFQHI7jxv6tM8uOv8AkC1BY2dR5MnqsnJm6odCBQr3AMNdgiMgofkpgHFjcWPJ6tAeROXYmS+R7MsgofkeRAwsQDJk9VjACD7M8WQC+u4KpazcCWJ2Zs+MEWsWJ/VoiChsagUj8ujuZOCvX4Zq6cuxuCPy6O4CSlF7JiROXk2pDJR71KyZb6GoDyZL6GoYQD0dxUUN7EvInLyXcCVJQ0dR5MfssaPyFHclSUNHUCcfkezKd+XQ1DLi/RFjygDXcCyQgobmRxse6Ma4ywJlD+QR+QJzrRixvRihA1/kJ+ysfkO4QgZ4Wo19jzLRsQhAp+1uc4ihA6sZ5DuZ4mo19jhAM60bE0rkvcUIEfx3/JnkFEiEIF4Ho1HmWjYhCBoRyW5H8d/yEIE5BxPUr+QOgYQgYA13OquS9xQgR/Hf8iyDieoQgaZVsX8iwNYowhA5yO5t/Hf8jhAnIOJ6mmVbF/IoQDA1ijM74t1HCA/5K6MyVq7EIQOjKti/kWBr6MUIEE8W6mn8hP39hCA0PJTc5ooQOnA1ijM74t1HCBf8hP2PGeQ7hCBniajX2L+QtGEIEo9GdZQH8hCB/9k="

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
    val userData = context.dataStore.data
        .map { profile ->
            ProfileData(
                userId = profile[UserDataKeys.USERID] ?: "abc",
                username = profile[UserDataKeys.USERNAME] ?: "default name",
                pfp = profile[UserDataKeys.PFP] ?: DEFAULT_PFP,
                header = profile[UserDataKeys.HEADER] ?: DEFAULT_HEADER
            )
        }

    suspend fun updateUserId(userId: String) {
        context.dataStore.edit { profile ->
            profile[UserDataKeys.USERID] = userId
        }
    }

    suspend fun updateUserName(username: String) {
        context.dataStore.edit { profile ->
            profile[UserDataKeys.USERNAME] = username
        }
    }

    suspend fun updateUserPfp(pfp: String) {
        context.dataStore.edit { profile ->
            profile[UserDataKeys.PFP] = pfp
        }
    }

    suspend fun updateUserHeader(header: String) {
        context.dataStore.edit { profile ->
            profile[UserDataKeys.HEADER] = header
        }
    }

    private object UserDataKeys {
        val USERID = stringPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val PFP = stringPreferencesKey("pfp")
        val HEADER = stringPreferencesKey("header")
    }
}