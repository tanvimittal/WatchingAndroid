package jp.kyuuki.watching.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import jp.kyuuki.watching.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_0
    ,R.string.tab_text_request
    ,R.string.tab_text_1
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    // タブカウント
    private val Count = 3
    override fun getItem(position: Int): Fragment{
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        var fragment : Fragment?=null
        when(position){
            0 -> fragment = Search()
            1 -> fragment = RequestRecieved()
            2 -> fragment = Chats()

        }

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return Count
    }
}