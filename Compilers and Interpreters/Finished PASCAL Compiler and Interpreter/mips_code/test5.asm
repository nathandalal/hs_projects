	.text
	.globl main
	
main:
	li $v0, 5
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 12
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procmax
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	li $v0, 13
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	li $v0, 7
	subu $sp, $sp, 4
	sw $v0, ($sp) # PUSH
	subu $sp, $sp, 4
	sw $ra, ($sp) # PUSH
	jal procmax
	lw $ra, ($sp)
	addu $sp, $sp, 4 # POP
	move $a0, $v0
	li $v0, 1
	syscall # prints number
	la $a0, nextln
	li $v0, 4
	syscall # moves to nextln
	
	li $v0 10
	syscall	# halt
	
procmax:
	li $t0, 0
	subu $sp, $sp, 4
	sw $t0, ($sp) # PUSH
	lw $v0, 12($sp)
	sw $v0, 0($sp)
	lw $v0, 8($sp)
	move $t0, $v0
	lw $v0, 12($sp)
	move $t1, $v0
	ble $t0, $t1, endif1
	lw $v0, 8($sp)
	sw $v0, 0($sp)
	j endif1
endif1:
	lw $t0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	lw $v0, ($sp)
	addu $sp, $sp, 4 # POP
	move $v0, $t0
	jr $ra # end of procmax
	
	.data
	
nextln:
	.asciiz "\n"
