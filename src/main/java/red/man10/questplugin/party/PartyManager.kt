package red.man10.questplugin.party

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object PartyManager {

    private val partyLeaders = mutableMapOf<UUID, MutableSet<UUID>>() // Leader -> Members
    val invited = mutableMapOf<UUID, UUID>() // Invited player -> Inviter

    fun createParty(player: Player): Boolean {
        if (isInParty(player)) return false
        partyLeaders[player.uniqueId] = mutableSetOf(player.uniqueId)
        return true
    }

    fun invitePlayer(inviter: Player, target: Player): Boolean {
        if (!isLeader(inviter)) return false
        if (isInParty(target)) return false
        invited[target.uniqueId] = inviter.uniqueId
        return true
    }

    fun joinParty(player: Player, leader: Player): Boolean {
        val invitedBy = invited[player.uniqueId] ?: return false
        if (invitedBy != leader.uniqueId) return false
        val members = partyLeaders[leader.uniqueId] ?: return false
        members.add(player.uniqueId)
        invited.remove(player.uniqueId)
        return true
    }

    fun leaveParty(player: Player): Boolean {
        partyLeaders.forEach { (leader, members) ->
            if (members.remove(player.uniqueId)) {
                if (leader == player.uniqueId || members.isEmpty()) {
                    disbandParty(leader)
                }
                return true
            }
        }
        return false
    }

    fun disbandParty(leader: UUID): Boolean {
        return partyLeaders.remove(leader) != null
    }

    fun kickMember(leader: Player, target: Player): Boolean {
        if (!isLeader(leader)) return false
        return partyLeaders[leader.uniqueId]?.remove(target.uniqueId) ?: false
    }

    fun isInParty(player: Player): Boolean {
        return partyLeaders.values.any { it.contains(player.uniqueId) }
    }

    fun isLeader(player: Player): Boolean {
        return partyLeaders.containsKey(player.uniqueId)
    }

    fun getPartyMembers(player: Player): Set<Player> {
        val entry = partyLeaders.entries.find { it.value.contains(player.uniqueId) } ?: return emptySet()
        return entry.value.mapNotNull { Bukkit.getPlayer(it) }.toSet()
    }

    fun getLeader(player: Player): Player? {
        val entry = partyLeaders.entries.find { it.value.contains(player.uniqueId) } ?: return null
        return Bukkit.getPlayer(entry.key)
    }
    // プレイヤーの所属パーティー（メンバー一覧）を取得
    fun getParty(player: Player): Set<Player>? {
        return partyLeaders.entries.find { it.value.contains(player.uniqueId) }?.value
            ?.mapNotNull { Bukkit.getPlayer(it) }
            ?.toSet()
    }

    // 招待承諾処理（acceptInvite の代わり）
    fun acceptInvite(player: Player): Boolean {
        val leaderUUID = invited[player.uniqueId] ?: return false
        val members = partyLeaders[leaderUUID] ?: return false
        members.add(player.uniqueId)
        invited.remove(player.uniqueId)
        return true
    }

}
